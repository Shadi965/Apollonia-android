@file:Suppress("UsingMaterialAndMaterial3Libraries")

package songbird.apollo.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import songbird.apollo.presentation.ui.screens.FavoriteGraph
import songbird.apollo.presentation.ui.screens.FavoriteGraph.FavoriteScreenRoute
import songbird.apollo.presentation.ui.screens.LibraryGraph
import songbird.apollo.presentation.ui.screens.LibraryGraph.LibraryScreenRoute
import songbird.apollo.presentation.ui.screens.LocalNavController
import songbird.apollo.presentation.ui.screens.SearchGraph
import songbird.apollo.presentation.ui.screens.SearchGraph.SearchScreenRoute
import songbird.apollo.presentation.ui.screens.SettingsScreenRoute
import songbird.apollo.presentation.ui.screens.SongMenuRoute
import songbird.apollo.presentation.ui.screens.favorites.FavoritesScreen
import songbird.apollo.presentation.ui.screens.library.LibraryScreen
import songbird.apollo.presentation.ui.screens.menu.SongActions
import songbird.apollo.presentation.ui.screens.scaffold.AppScaffold
import songbird.apollo.presentation.ui.screens.search.SearchScreen
import songbird.apollo.presentation.ui.screens.settings.SettingsScreen
import songbird.apollo.presentation.ui.theme.ApolloniaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApolloniaTheme(dynamicColor = false) {
                NavApp()
            }
        }
    }
}

@Composable
fun NavApp() {
    val sheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(sheetNavigator)

    CompositionLocalProvider(
        LocalNavController provides navController,

        ) {
        ModalBottomSheetLayout(
            bottomSheetNavigator = sheetNavigator
        ) {
            AppScaffold { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = SearchGraph,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    enterTransition = { fadeIn(animationSpec = tween(durationMillis = 100)) },
                    exitTransition = { fadeOut(animationSpec = tween(durationMillis = 80)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 100)) },
                    popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 80)) },
                ) {
                    navigation<FavoriteGraph>(
                        startDestination = FavoriteScreenRoute
                    ) {
                        composable<FavoriteScreenRoute> { FavoritesScreen() }
                    }
                    navigation<LibraryGraph>(startDestination = LibraryScreenRoute) {
                        composable<LibraryScreenRoute> { LibraryScreen() }
                    }
                    navigation<SearchGraph>(startDestination = SearchScreenRoute) {
                        composable<SearchScreenRoute> { SearchScreen() }
                    }
                    composable<SettingsScreenRoute> { SettingsScreen() }

                    bottomSheet<SongMenuRoute> { backStackEntry ->
                        val route: SongMenuRoute = backStackEntry.toRoute()
                        SongActions(
                            songId = route.songId,
                            currentPlaylistId = route.currentPlaylistId,
                        )
                    }
                }
            }
        }
    }
}