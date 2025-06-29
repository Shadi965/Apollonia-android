package songbird.apollo.presentation.ui.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import songbird.apollo.domain.usecase.AddSongToFavoritesUseCase
import songbird.apollo.domain.usecase.GetAlbumByIdUseCase
import songbird.apollo.domain.usecase.GetSongByIdUseCase
import songbird.apollo.domain.usecase.IsSongFavoriteUseCase
import songbird.apollo.domain.usecase.RemoveSongFromFavoritesUseCase
import songbird.apollo.presentation.model.toUi

@HiltViewModel(assistedFactory = SongActionsViewModel.Factory::class)
class SongActionsViewModel @AssistedInject constructor(
    @Assisted("song_id") private val songId: Int,
    @Assisted("title") private val title: String,
    @Assisted("artist") private val artist: String,
    @Assisted("album_id") private val albumId: Int,
    @Assisted("cover_url") private val coverUrl: String?,
    @Assisted("current_playlist_id") private val currentPlaylistId: Int?,
    private val getSongById: GetSongByIdUseCase,
    private val addToFavorites: AddSongToFavoritesUseCase,
    private val removeFromFavorites: RemoveSongFromFavoritesUseCase,
    private val getAlbum: GetAlbumByIdUseCase,
    private val isSongFavorite: IsSongFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ScreenState(
        title = title,
        artist = artist,
        coverUrl = coverUrl,
    ))
    val state: StateFlow<ScreenState> get() = _state

    init {
        // TODO: Думаю следует разбить различные данные на отдельные flow и собирать с помощью combine
        // TODO: а от этого бардака избавиться
        // TODO: Favorites - playlist 1
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    albumName = getAlbum(albumId).title,
                    isFavorite = isSongFavorite(songId)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = true)
            }
        }
        viewModelScope.launch {
            try {
                // TODO: isDownloaded
                val song = getSongById(songId).toUi(isDownloaded = false)
                _state.value = _state.value.copy(
                    title = song.title,
                    artist = song.artist,
                    coverUrl = song.coverUrl,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = true)
            }
        }
    }

    private fun addToFavorites() {
        try {
            _state.value = _state.value.copy(isFavorite = true)
            addToFavorites(songId)
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = true, isFavorite = false)
        }
    }

    private fun removeFromFavorites() {
        try {
            _state.value = _state.value.copy(isFavorite = false)
            removeFromFavorites(songId)
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = true, isFavorite = true)
        }
    }

    fun toggleFavorite() {
        if (_state.value.isFavorite) removeFromFavorites()
        else addToFavorites()
    }

    data class ScreenState(
        val title: String = "",
        val artist: String = "",
        val albumName: String = "",
        val coverUrl: String? = null,
        val isFavorite: Boolean = false,
        val error: Boolean = false
    )

    @AssistedFactory
    interface Factory {
        fun create(
        @Assisted("song_id") songId: Int,
        @Assisted("title") title: String,
        @Assisted("artist") artist: String,
        @Assisted("album_id") albumId: Int,
        @Assisted("cover_url") coverUrl: String?,
        @Assisted("current_playlist_id") currentPlaylistId: Int?
        ): SongActionsViewModel
    }
}