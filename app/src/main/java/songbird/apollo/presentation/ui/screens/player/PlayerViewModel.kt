package songbird.apollo.presentation.ui.screens.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import songbird.apollo.data.BackendException
import songbird.apollo.domain.usecase.GetSongByIdUseCase
import songbird.apollo.domain.usecase.GetSongStreamUrlUseCase
import songbird.apollo.presentation.model.SongUi
import songbird.apollo.presentation.model.toUi
import songbird.apollo.presentation.ui.LoadResult
import songbird.apollo.presentation.ui.LoadResult.Error
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success

@HiltViewModel(assistedFactory = PlayerViewModel.Factory::class)
class PlayerViewModel @AssistedInject constructor(
    @Assisted private val songId: Int,
    @ApplicationContext private val context: Context,
    private val getSongById: GetSongByIdUseCase,
    private val getSongStreamUrl: GetSongStreamUrlUseCase
) : ViewModel() {


    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _song = MutableStateFlow<LoadResult<SongUi>>(Loading)
    val song: StateFlow<LoadResult<SongUi>> get() = _song

    private var player: ExoPlayer? = null

    init {
        try {
            // TODO: Диспатчер следует задать у источника
            viewModelScope.launch(Dispatchers.IO) {
                _song.value = Success(getSongById(songId).toUi())
            }
        } catch (ex: BackendException) {
            _song.value = Error(ex, "Server error")
        }
    }


    fun preparePlayer() {
        val url = getSongStreamUrl(songId)
        releasePlayer()

        player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
            })
        }
    }

    fun playPause() {
        player?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {
        fun create(songId: Int): PlayerViewModel
    }
}