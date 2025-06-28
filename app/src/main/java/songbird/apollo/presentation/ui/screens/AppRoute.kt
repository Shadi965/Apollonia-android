package songbird.apollo.presentation.ui.screens

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute

@Serializable
sealed interface AppGraph

@Serializable
data object FavoriteGraph : AppGraph {
    @Serializable
    data object FavoriteScreenRoute : AppRoute
}

@Serializable
data object LibraryGraph : AppGraph {
    @Serializable
    data object LibraryScreenRoute : AppRoute
}

@Serializable
data object SearchGraph : AppGraph {
    @Serializable
    data object SearchScreenRoute : AppRoute
}

@Serializable
data object SettingsScreenRoute : AppRoute

@Serializable
data class SongMenuRoute(
    val songId: Int,
    val currentPlaylistId: Int? = null,
) : AppRoute

@Serializable
data class PlayerScreenRoute(
    val songId: Int,
) : AppRoute


