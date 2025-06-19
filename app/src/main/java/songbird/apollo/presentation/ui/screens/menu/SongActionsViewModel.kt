package songbird.apollo.presentation.ui.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import songbird.apollo.domain.usecase.AddSongToFavoritesUseCase
import songbird.apollo.domain.usecase.GetAlbumByIdUseCase
import songbird.apollo.domain.usecase.GetSongByIdUseCase
import songbird.apollo.domain.usecase.IsSongFavoriteUseCase
import songbird.apollo.domain.usecase.RemoveSongFromFavoritesUseCase
import songbird.apollo.presentation.model.SongUi
import songbird.apollo.presentation.model.toUi
import songbird.apollo.presentation.ui.LoadResult
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success

@HiltViewModel(assistedFactory = SongActionsViewModel.Factory::class)
class SongActionsViewModel @AssistedInject constructor(
    @Assisted private val songId: Int,
    @Assisted private val currentPlaylistId: Int?,
    private val getSongById: GetSongByIdUseCase,
    private val addToFavorites: AddSongToFavoritesUseCase,
    private val removeFromFavorites: RemoveSongFromFavoritesUseCase,
    private val getAlbum: GetAlbumByIdUseCase,
    private val isSongFavorite: IsSongFavoriteUseCase
) : ViewModel() {

    private val _song = MutableStateFlow(ScreenState())
    val song: StateFlow<ScreenState> get() = _song

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: isDownloaded
                val song = getSongById(songId).toUi(isDownloaded = false)
                val albumName = getAlbum(song.albumId).first()?.title ?: ""
                // TODO: Favorites - playlist 1
                _song.value = ScreenState(
                    song = Success(song),
                    albumName = albumName,
                    isFavorite = isSongFavorite(songId)
                )
            } catch (e: Exception) {
                _song.value = _song.value.copy(error = true)
            }
        }
    }

    fun addToFavorites() {
        viewModelScope.launch {
            try {
                addToFavorites(songId)
                _song.value = _song.value.copy(isFavorite = true)
            } catch (e: Exception) {
                _song.value = _song.value.copy(error = true)
            }
        }
    }

    fun removeFromFavorites() {
        viewModelScope.launch {
            try {
                removeFromFavorites(songId)
                _song.value = _song.value.copy(isFavorite = false)
            } catch (e: Exception) {
                _song.value = _song.value.copy(error = true)
            }
        }
    }

    fun play() {

    }

    data class ScreenState(
        val song: LoadResult<SongUi> = Loading,
        val albumName: String = "",
        val isFavorite: Boolean = false,
        val error: Boolean = false
    )

    @AssistedFactory
    interface Factory {
        fun create(songId: Int, currentPlaylistId: Int?): SongActionsViewModel
    }
}