package songbird.apollo.presentation.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    SearchScreenContent(
        modifier = modifier.fillMaxSize(),
        getState = { state.value },
        onClick = { viewModel.load() })
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier = Modifier,
    getState: () -> LoadResult<List<SongPreviewUi>>,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val screenState = getState()) {
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
                LazyColumn(modifier = modifier) {
                    items(screenState.data) { song ->
                        SongItem(
                            song = song
                        )
                    }
                }
            }

            else -> {
                // TODO: Ошибка загрузки списка
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ошибочка вышла"
                    )
                    Button(onClick = onClick) {
                        Text(text = "Загрузить")
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
        getState = { Success(songs) }) {}
}