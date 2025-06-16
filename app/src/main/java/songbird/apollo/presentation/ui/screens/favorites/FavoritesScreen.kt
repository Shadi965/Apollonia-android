package songbird.apollo.presentation.ui.screens.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun FavoritesScreen(modifier: Modifier = Modifier) {
    ModifyScaffoldUi(
        topBarTitle = R.string.favorite,
    )

    val viewModel: FavoritesScreenViewModel = hiltViewModel()

    val favorites = viewModel.favorite.collectAsState()
    FavoritesScreenContent(
        modifier = modifier.fillMaxSize(),
        favorites = favorites.value
    )
}

@Composable
private fun FavoritesScreenContent(
    modifier: Modifier = Modifier,
    favorites: LoadResult<List<SongPreviewUi>>
) {
    when (favorites) {

        is Loading, is Empty  -> {
            Text(
                text = "Пусто"
            )
        }

        is Success -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favorites.data) { song ->
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