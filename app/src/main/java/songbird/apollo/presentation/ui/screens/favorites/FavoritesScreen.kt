package songbird.apollo.presentation.ui.screens.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import songbird.apollo.R
import songbird.apollo.presentation.ui.screens.scaffold.ModifyScaffoldUi

@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    ModifyScaffoldUi(
        topBarTitle = R.string.favorite,
    )
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("FavScreen")
    }
}