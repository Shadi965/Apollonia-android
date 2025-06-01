package songbird.apollo.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import songbird.apollo.R
import songbird.apollo.presentation.ui.screens.FavoriteGraph
import songbird.apollo.presentation.ui.screens.FavoriteGraph.FavoriteScreenRoute
import songbird.apollo.presentation.ui.screens.LibraryGraph
import songbird.apollo.presentation.ui.screens.LibraryGraph.LibraryScreenRoute
import songbird.apollo.presentation.ui.screens.LocalNavController
import songbird.apollo.presentation.ui.screens.SearchGraph
import songbird.apollo.presentation.ui.screens.SearchGraph.SearchScreenRoute
import songbird.apollo.presentation.ui.screens.favorites.FavoritesScreen
import songbird.apollo.presentation.ui.screens.library.LibraryScreen
import songbird.apollo.presentation.ui.screens.scaffold.BottomNavBar
import songbird.apollo.presentation.ui.screens.scaffold.MainTabs
import songbird.apollo.presentation.ui.screens.scaffold.NavigateUpAction
import songbird.apollo.presentation.ui.screens.scaffold.TopToolBar
import songbird.apollo.presentation.ui.screens.search.SearchScreen
import songbird.apollo.presentation.ui.theme.ApolloniaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApolloniaTheme {
                NavApp()
            }
        }
    }
}

@Composable
fun NavApp() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val destination = currentBackStackEntry.value?.destination
    val topBarTitleRes = when {
        destination?.hasRoute(FavoriteScreenRoute::class) == true -> R.string.favorite
        destination?.hasRoute(LibraryScreenRoute::class) == true -> R.string.library
        destination?.hasRoute(SearchScreenRoute::class) == true -> R.string.search
        else -> R.string.app_name
    }
    Scaffold(
        topBar = {
            TopToolBar(
                navigateUp = if (navController.previousBackStackEntry == null) {
                    NavigateUpAction.Hidden
                } else {
                    NavigateUpAction.Visible {
                        navController.navigateUp()
                    }
                },
                title = topBarTitleRes
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                tabs = MainTabs
            )
        }
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalNavController provides navController
        ) {
            NavHost(
                navController = navController,
                startDestination = FavoriteGraph,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
            }
        }
    }
}