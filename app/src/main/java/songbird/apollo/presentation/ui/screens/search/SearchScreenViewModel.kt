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
import songbird.apollo.data.NoConnectionException
import songbird.apollo.data.ParseBackendResponseException
import songbird.apollo.domain.usecase.SearchSongsUseCase
import songbird.apollo.presentation.model.SongPreviewUi
import songbird.apollo.presentation.model.toUi
import songbird.apollo.presentation.ui.LoadResult
import songbird.apollo.presentation.ui.LoadResult.Loading
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchSongsUseCase: SearchSongsUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _foundSongs: MutableStateFlow<LoadResult<List<SongPreviewUi>>> = MutableStateFlow(Loading)
    val foundSongs: StateFlow<LoadResult<List<SongPreviewUi>>> get() = _foundSongs

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _foundSongs.value = LoadResult.Empty
                        return@collectLatest
                    }

                    _foundSongs.value = Loading
                    try {
                        val songs = searchSongsUseCase(query)
                        _foundSongs.value = if (songs.isEmpty()) LoadResult.Empty
                        else LoadResult.Success(songs.map { it.toUi() })
                    } catch (e: NoConnectionException) {
                        _foundSongs.value = LoadResult.Error("No internet connection")
                    } catch (e: ParseBackendResponseException) {
                        _foundSongs.value = LoadResult.Error("App version is outdated. Please update.")
                    } catch (e: BackendException) {
                        _foundSongs.value = LoadResult.Error("Server error.")
                    }
                }
        }
    }

}