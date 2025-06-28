package songbird.apollo.presentation.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import songbird.apollo.data.BackendException
import songbird.apollo.data.ParseBackendResponseException
import songbird.apollo.domain.usecase.GetAllSongsUseCase
import songbird.apollo.domain.usecase.SearchSongsUseCase
import songbird.apollo.presentation.model.SongPreviewUi
import songbird.apollo.presentation.model.toUi
import songbird.apollo.presentation.ui.LoadResult
import songbird.apollo.presentation.ui.LoadResult.Empty
import songbird.apollo.presentation.ui.LoadResult.Error
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success
import java.net.ConnectException
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    searchSongsUseCase: SearchSongsUseCase,
    private val getAllSongs: GetAllSongsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _foundSongs: MutableStateFlow<LoadResult<List<SongPreviewUi>>> = MutableStateFlow(Empty)
    val foundSongs: StateFlow<LoadResult<List<SongPreviewUi>>> get() = _foundSongs

    fun onSearchQueryChange(query: String) {
        if (query.isNotBlank()) _foundSongs.value = Loading
        _searchQuery.value = query
    }

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _foundSongs.value = Empty
                        return@collectLatest
                    }

                    try {
                        val songs = searchSongsUseCase(query)
                        _foundSongs.value = if (songs.isEmpty()) Empty
                        else Success(songs.map { it.toUi() })
                    } catch (ex: ConnectException) {
                        _foundSongs.value = Error(ex, "No internet connection")
                    } catch (ex: ParseBackendResponseException) {
                        _foundSongs.value = Error(ex, "App version is outdated. Please update.")
                    } catch (ex: BackendException) {
                        _foundSongs.value = Error(ex, "Server error.")
                    }
                }
        }
    }

    // TODO: Временно для тестов
    fun loadAllSongs() {
        _foundSongs.value = Loading
        viewModelScope.launch {
            try {
                _foundSongs.value = Success(getAllSongs().map { it.toUi() })
            } catch (ex: ConnectException) {
                _foundSongs.value = Error(ex, "No internet connection")
            } catch (ex: ParseBackendResponseException) {
                _foundSongs.value = Error(ex, "App version is outdated. Please update.")
            } catch (ex: BackendException) {
                _foundSongs.value = Error(ex, "Server error.")
            }
        }
    }

}
