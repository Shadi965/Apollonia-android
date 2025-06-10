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
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success
import songbird.apollo.presentation.ui.screens.SongItem
import songbird.apollo.presentation.ui.screens.scaffold.ModifyScaffoldUi

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    ModifyScaffoldUi(
        topBarTitle = R.string.search,
    )

    val viewModel: SearchScreenViewModel = hiltViewModel()
    val state = viewModel.foundSongs.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()
    SearchScreenContent(
        modifier = modifier.fillMaxSize(),
        state = state.value,
        onSearch = { viewModel.onSearchQueryChange(it) },
        searchQuery = searchQuery.value
    )
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier = Modifier,
    state: LoadResult<List<SongPreviewUi>> = Loading,
    onSearch: (String) -> Unit = {},
    searchQuery: String = ""
) {
    Column(
        modifier = modifier
    ) {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = searchQuery,
            onValueChange = onSearch,
            label = { Text(text = "Search") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { focusManager.clearFocus() }
            ),
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
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
            // TODO: Обновить состояния
            when (state) {
                is Loading -> {
                    Text(
                        text = "Грузится"
                    )
                }

                is Empty -> {
                    Text(
                        text = "Пусто"
                    )
                }

                is Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.data) { song ->
                            SongItem(
                                song = song
                            )
                        }
                    }
                }

                else -> {
                    // TODO: Ошибка загрузки списка
                    Box(
                        modifier = modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ошибочка вышла"
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
        state = Success(songs))
}