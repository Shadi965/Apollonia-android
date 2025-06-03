package songbird.apollo.presentation.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import songbird.apollo.data.BackendException
import songbird.apollo.data.NoConnectionException
import songbird.apollo.data.ParseBackendResponseException
import songbird.apollo.domain.usecase.GetAllSongsUseCase
import songbird.apollo.presentation.model.SongPreviewUi
import songbird.apollo.presentation.model.toUi
import songbird.apollo.presentation.ui.LoadResult
import songbird.apollo.presentation.ui.LoadResult.Loading
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase
) : ViewModel() {
    private val _foundSongs: MutableStateFlow<LoadResult<List<SongPreviewUi>>> = MutableStateFlow(Loading)
    val foundSongs: StateFlow<LoadResult<List<SongPreviewUi>>> get() = _foundSongs

    fun load() {
        _foundSongs.value = Loading
        // TODO: Разобраться с исключениями
        viewModelScope.launch {
            try {
                val songs = getAllSongsUseCase()
                _foundSongs.value = if (songs.isEmpty()) LoadResult.Empty
                else LoadResult.Success(songs.map { it.toUi() })
            } catch (e: NoConnectionException) {
                _foundSongs.value = LoadResult.Error("No internet connection")
            } catch (e: ParseBackendResponseException) {
                _foundSongs.value = LoadResult.Error("Looks like this version of the app is no longer compatible with the server. Please update to continue.")
            } catch (e: BackendException) {
                _foundSongs.value = LoadResult.Error("Looks like this version of the app is no longer compatible with the server. Please update to continue.")
            }
        }
    }

    init {
        load()
    }

}