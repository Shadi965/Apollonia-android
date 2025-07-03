package songbird.apollo.presentation.ui.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import songbird.apollo.domain.usecase.AddSongToFavoritesUseCase
import songbird.apollo.domain.usecase.GetAlbumByIdUseCase
import songbird.apollo.domain.usecase.IsSongFavoriteUseCase
import songbird.apollo.domain.usecase.RemoveSongFromFavoritesUseCase
import songbird.apollo.presentation.model.SongPreviewUi

@HiltViewModel(assistedFactory = SongActionsViewModel.Factory::class)
class SongActionsViewModel @AssistedInject constructor(
    @Assisted private val song: SongPreviewUi,
    private val addToFavorites: AddSongToFavoritesUseCase,
    private val removeFromFavorites: RemoveSongFromFavoritesUseCase,
    private val getAlbum: GetAlbumByIdUseCase,
    private val isSongFavorite: IsSongFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ScreenState(
        title = song.title,
        artist = song.artist,
        coverUrl = song.coverUrl,
    ))
    val state: StateFlow<ScreenState> get() = _state

    init {
        // TODO: Favorites - playlist 1
        viewModelScope.launch {
            try {
                launch { _state.update { it.copy(isFavorite = isSongFavorite(song.id)) } }
                launch { _state.update { it.copy(albumName = getAlbum(song.albumId).title) } }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = true)
            }
        }
    }

    private fun addToFavorites() {
        try {
            _state.value = _state.value.copy(isFavorite = true)
            addToFavorites(song.id)
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = true, isFavorite = false)
        }
    }

    private fun removeFromFavorites() {
        try {
            _state.value = _state.value.copy(isFavorite = false)
            removeFromFavorites(song.id)
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
        fun create(song: SongPreviewUi): SongActionsViewModel
    }
}