package songbird.apollo.presentation.ui.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import songbird.apollo.R
import songbird.apollo.presentation.model.SongPreviewUi
import songbird.apollo.presentation.ui.LoadResult
import songbird.apollo.presentation.ui.LoadResult.Empty
import songbird.apollo.presentation.ui.LoadResult.Error
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success
import songbird.apollo.presentation.ui.screens.LocalNavController
import songbird.apollo.presentation.ui.screens.PlayerScreenRoute
import songbird.apollo.presentation.ui.screens.SongItem
import songbird.apollo.presentation.ui.screens.SongMenuRoute
import songbird.apollo.presentation.ui.screens.scaffold.ModifyScaffoldUi

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    ModifyScaffoldUi(
        topBarTitle = R.string.search,
    )

    val navController = LocalNavController.current
    val viewModel: SearchScreenViewModel = hiltViewModel()

    val songsState by viewModel.foundSongs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    SearchScreenContent(
        modifier = modifier.fillMaxSize(),
        songsState = songsState,
        searchQuery = searchQuery,
        onSearch = { viewModel.onSearchQueryChange(it) },
        onSongMoreClick = { song ->
            navController.navigate(
                SongMenuRoute(
                    songId = song.id,
                    title = song.title,
                    artist = song.artist,
                    albumId = song.albumId,
                    coverUrl = song.coverUrl
                )
            )
        },
        onSongClick = { songId ->
            navController.navigate(
                PlayerScreenRoute(songId)
            )
        },
        onLoadAllSongs = { viewModel.loadAllSongs() }
    )
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier = Modifier,
    songsState: LoadResult<List<SongPreviewUi>> = Empty,
    onSearch: (String) -> Unit = {},
    onSongMoreClick: (song: SongPreviewUi) -> Unit = {},
    onSongClick: (songId: Int) -> Unit = {},
    onLoadAllSongs: () -> Unit = {},
    searchQuery: String = ""
) {

    val focusManager = LocalFocusManager.current
    var songs by remember { mutableStateOf<List<SongPreviewUi>>(emptyList()) }
    var loadAll by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = searchQuery,
            onValueChange = onSearch,
            // TODO: вынести в ресурсы
            label = { Text(text = "Search") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { focusManager.clearFocus() }
            ),
            trailingIcon = {
                // TODO: Для тестов
                if (searchQuery.isEmpty()) {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        if (!loadAll) {
                            onLoadAllSongs()
                            loadAll = true
                    }
                    }) {
                        Icon(
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = null
                        )
                    }
                } else {
                    loadAll = false
                    IconButton(onClick = { onSearch("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            // TODO: Добавить описание
                            contentDescription = null
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors().copy(
                focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Надо покрасивее переписать
            when (songsState) {
                is Empty -> {
                    songs = emptyList()
                    Text(
                        text = if (searchQuery.isEmpty()) "Search songs" else "No songs found"
                    )
                }

                is Success, is Loading -> {
                    (songsState as? Success)?.data?.let {
                        songs = it
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(songs) { song ->
                            SongItem(
                                song = song,
                                onMoreClick = {
                                    focusManager.clearFocus()
                                    onSongMoreClick(it)
                                },
                                onClick = onSongClick
                            )
                        }
                    }
                }

                is Error -> {
                    Box(
                        modifier = modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = songsState.message ?: "Unknown error",
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    val songs by remember {
        mutableStateOf(List(100) { i ->
            SongPreviewUi(
                id = i,
                title = "Song $i",
                artist = "Artist $i",
                albumId = i,
                coverUrl = null,
                position = 0.0,
                isDownloaded = i % 2 == 0
            )
        })
    }
    SearchScreenContent(
        songsState = Success(songs)
    )
}