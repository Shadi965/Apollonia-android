package songbird.apollo.presentation.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import songbird.apollo.data.BackendException
import songbird.apollo.data.ParseBackendResponseException
import songbird.apollo.domain.usecase.GetFavoritesUseCase
import songbird.apollo.presentation.model.toUi
import songbird.apollo.presentation.ui.LoadResult.Empty
import songbird.apollo.presentation.ui.LoadResult.Error
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class FavoritesScreenViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase
) : ViewModel() {

    val favorite = getFavoritesUseCase()
        .map { songs ->
            if (songs.isEmpty())
                Empty
            else
                Success(songs.map { it.toUi() })
        }.catch {
            when (it) {
                is ConnectException -> emit(Error(it, "No internet connection"))
                is ParseBackendResponseException -> emit(Error(it, "App version is outdated. Please update."))
                is BackendException -> emit(Error(it, "Server error."))
                else -> throw it
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Loading
        )
}