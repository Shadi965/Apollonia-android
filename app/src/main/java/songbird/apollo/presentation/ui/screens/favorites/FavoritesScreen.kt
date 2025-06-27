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
import songbird.apollo.presentation.ui.LoadResult.Error
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success
import songbird.apollo.presentation.ui.screens.LocalNavController
import songbird.apollo.presentation.ui.screens.SongItem
import songbird.apollo.presentation.ui.screens.SongMenuRoute
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
    val navController = LocalNavController.current
    when (favorites) {

        is Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Empty"
                )
            }
        }

        is Success -> {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(favorites.data) { song ->
                    SongItem(
                        song = song,
                        onMoreClick = {
                            navController.navigate(
                                // TODO: Favorites - playlist 1
                                SongMenuRoute(
                                    songId = song.id,
                                    currentPlaylistId = 1
                                )
                            )
                        },
                    )
                }
            }
        }

        is Loading -> {
            // TODO: Здесь ничего не должно отображаться, нужно анимацией запуска прикрыть загрузку данных
        }

        is Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = favorites.message?: "Unknown error",
                )
            }
        }
    }
}