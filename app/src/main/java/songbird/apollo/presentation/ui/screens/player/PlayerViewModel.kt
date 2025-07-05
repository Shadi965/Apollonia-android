package songbird.apollo.presentation.ui.screens.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import songbird.apollo.domain.model.LyricLine
import songbird.apollo.domain.usecase.GetLyricsUseCase
import songbird.apollo.domain.usecase.GetSongStreamUrlUseCase
import songbird.apollo.presentation.model.SongPreviewUi
import songbird.apollo.presentation.ui.LoadResult
import songbird.apollo.presentation.ui.LoadResult.Empty
import songbird.apollo.presentation.ui.LoadResult.Error
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success
import javax.inject.Inject

@OptIn(UnstableApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getSongStreamUrl: GetSongStreamUrlUseCase,
    private val getLyrics: GetLyricsUseCase
) : ViewModel() {

    val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _state = MutableStateFlow(
        PlayerState(
            songs = emptyList(),
            order = emptyList(),
            index = 0,
            isPlaying = false,
            isShuffled = false,
        )
    )
    val state: StateFlow<PlayerState> get() = _state

    private val _lyrics = MutableStateFlow<LoadResult<List<LyricLine>>>(Loading)
    val lyrics: StateFlow<LoadResult<List<LyricLine>>> get() = _lyrics

    val playlist: StateFlow<List<SongPreviewUi>> = _state.map { state ->
        if (!state.isShuffled) {
            state.songs
        } else {
            state.order.map { index -> state.songs[index] }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    private val _pbPosition = MutableStateFlow(0L)
    val pbPosition: StateFlow<Long> get() = _pbPosition

    val currentSong: StateFlow<SongPreviewUi> = combine(playlist, state) { list, state ->
        val index = state.index
        if (index in list.indices) {
            list[index]
        } else {
            SongPreviewUi(0, "", "", 0, 0, 0.0, null, false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SongPreviewUi(0, "", "", 0, 0, 0.0, null, false)
    )

    private val pbPositionListener = viewModelScope.launch {
        while (true) {
            _pbPosition.value = player.currentPosition
            delay(30)
        }
    }

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
                super.onIsPlayingChanged(isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                when (reason) {
                    Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                        _state.update { it.incrementIndex().copy(isPlaying = true) }
                    }

                    Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
                        _state.update { it.copy(index = 0) }
                    }

                    else -> {}
                }
                super.onMediaItemTransition(mediaItem, reason)
            }

        })
    }

    fun setPlaylist(
        songs: List<SongPreviewUi>,
        startIndex: Int = 0,
        shuffle: Boolean = false,
        autoPlay: Boolean = true
    ) {
        if (player.isReleased) return

        player.stop()

        _pbPosition.value = 0

        _state.value = PlayerState(
            songs = songs,
            order = songs.indices.toList(),
            index = startIndex,
            isPlaying = false,
            isShuffled = false,
        )

        if (shuffle) shuffle(continueCurrent = false, autoPlay = autoPlay)
        else with(player) {
            val mediaItems = songs
                .map { it.toMediaItem() }
            setMediaItems(mediaItems, startIndex, 0)
            playWhenReady = autoPlay
            prepare()
        }
    }

    fun playToggle() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun next() {
        _state.update { it.incrementIndex() }
        _pbPosition.value = 0
        player.seekToNext()
    }

    fun previous() {
        _state.update { it.decrementIndex() }
        _pbPosition.value = 0
        player.seekToPrevious()
    }

    fun skipTo(index: Int) {
        _state.update { it.copy(index = index) }
        _pbPosition.value = 0
        player.seekTo(index, 0)
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        _pbPosition.value = position
    }

    fun addNext(song: SongPreviewUi) {
        val nextIndex = _state.value.index + 1
        val state = _state.value
        state.updatePlaylist { it.toMutableList().apply { add(nextIndex, song) } }
        state.updateOrder {
            it.map { index ->
                if (index >= nextIndex) index + 1
                else index
            }.toMutableList().apply { add(nextIndex, nextIndex) }
        }
        _state.value = state
        player.addMediaItem(nextIndex, song.toMediaItem())
    }

    fun addLast(song: SongPreviewUi) {
        val state = _state.value
        state.updatePlaylist { it.toMutableList().apply { add(song) } }
        state.updateOrder { it.toMutableList().apply { add(size) } }
        _state.value = state
        player.addMediaItem(song.toMediaItem())
    }

    fun shuffle(continueCurrent: Boolean = true, autoPlay: Boolean = _state.value.isPlaying) {
        var state = _state.value
        state = if (!state.isShuffled) {
            val newOrder = if (continueCurrent) {
                val shuffled = state.order.toMutableList()
                shuffled.removeAt(state.index)
                shuffled.shuffle()
                shuffled.add(0, state.index)
                shuffled
            } else {
                val shuffled = state.order.toMutableList()
                shuffled.shuffle()
                shuffled
            }

            state.copy(
                order = newOrder,
                isShuffled = true,
                index = if (continueCurrent) 0 else state.index
            )
        } else {
            val originalIndex = state.order[state.index]
            state.copy(
                order = state.order.indices.toList(),
                index = originalIndex,
                isShuffled = false
            )
        }

        _state.value = state

        val mediaItems = state.order
            .map { index -> state.songs[index].toMediaItem() }

        with(player) {
            setMediaItems(
                mediaItems,
                state.index,
                currentPosition
            )
            playWhenReady = autoPlay
            prepare()
        }
    }

    private fun releasePlayer() {
        pbPositionListener.cancel()
        player.release()

        _state.update {
            it.copy(
                songs = emptyList(),
                order = emptyList(),
                index = 0,
                isPlaying = false,
                isShuffled = false,
            )
        }
    }

    override fun onCleared() {
        releasePlayer()
    }

    data class PlayerState(
        val songs: List<SongPreviewUi>,
        val order: List<Int>,
        val index: Int,
        val isPlaying: Boolean,
        val isShuffled: Boolean,
    ) {
        fun updatePlaylist(func: (List<SongPreviewUi>) -> List<SongPreviewUi>) =
            copy(songs = func(songs))

        fun updateOrder(func: (List<Int>) -> List<Int>) =
            copy(order = func(order))

        fun incrementIndex(): PlayerState {
            return if (index < songs.size - 1)
                copy(index = (index + 1))
            else
                this
        }

        fun decrementIndex(): PlayerState {
            return if (index > 0)
                copy(index = (index - 1))
            else
                this
        }
    }

    private fun SongPreviewUi.toMediaItem(streamUrl: String = getSongStreamUrl(id)) =
        MediaItem.Builder()
            .setUri(streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setDurationMs(duration.toLong() * 1000L)
                    .setArtworkUri(coverUrl?.toUri())
                    .build()
            ).build()

    fun loadLyrics() {
        viewModelScope.launch {
            _lyrics.value = Loading
            _lyrics.value = try {
                val lyrics = getLyrics(currentSong.value.id)
                if (lyrics.isNotEmpty()) Success(lyrics)
                else Empty
            } catch (e: Exception) {
                Error(e)
            }
        }
    }

}
