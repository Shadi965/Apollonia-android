package songbird.apollo.presentation.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import songbird.apollo.domain.usecase.GetFavoritesUseCase
import songbird.apollo.presentation.model.toUi
import songbird.apollo.presentation.ui.LoadResult
import javax.inject.Inject

@HiltViewModel
class FavoritesScreenViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase
) : ViewModel() {

    val favorite = getFavoritesUseCase()
        .map { songs ->
            if (songs.isEmpty())
                LoadResult.Empty
            else
                LoadResult.Success(songs.map { it.toUi() })
    }.catch {
        emit(LoadResult.Error(it.message ?: "Error"))
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = LoadResult.Loading)

//    val favorite: StateFlow<LoadResult<List<SongPreviewUi>>> get() = _favorite

}