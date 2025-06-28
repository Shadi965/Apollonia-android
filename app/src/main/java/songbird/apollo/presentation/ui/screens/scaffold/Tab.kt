package songbird.apollo.presentation.ui.screens.scaffold

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.persistentListOf
import songbird.apollo.R
import songbird.apollo.presentation.ui.screens.AppGraph
import songbird.apollo.presentation.ui.screens.FavoriteGraph
import songbird.apollo.presentation.ui.screens.LibraryGraph
import songbird.apollo.presentation.ui.screens.SearchGraph

data class AppTab(
    val icon: ImageVector,
    @StringRes val labelRes: Int,
    val graph: AppGraph,
    val name: String? = graph::class.qualifiedName
)

val MainTabs = persistentListOf(
    AppTab(
        icon = Icons.Default.LibraryMusic,
        labelRes = R.string.library,
        graph = LibraryGraph
    ),
    AppTab(
        icon = Icons.Default.Favorite,
        labelRes = R.string.favorite,
        graph = FavoriteGraph
    ),
    AppTab(
        icon = Icons.Default.Search,
        labelRes = R.string.search,
        graph = SearchGraph
    )
)