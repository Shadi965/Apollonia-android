package songbird.apollo.presentation.ui.screens.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import songbird.apollo.presentation.ui.screens.LocalNavController
import songbird.apollo.presentation.ui.screens.SettingsScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val navController = LocalNavController.current

    val scaffoldViewModel: ScaffoldViewModel = hiltViewModel()
    val uiState by scaffoldViewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (uiState.showTopBar) {
                TopToolBar(
                    navigateUp = uiState.showNavigateUp,
                    title = uiState.topBarTitle,
                    toSettingsScreen = NavigateAction.Enable {
                        navController.navigate(SettingsScreenRoute)
                    },
                    showSettingsButton = uiState.showSettingsButton,
                    scrollBehavior = scrollBehavior
                )
            }
        }, bottomBar = {
            if (uiState.showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    tabs = MainTabs
                )
            }
        }, content = { innerPadding ->
            CompositionLocalProvider(
                LocalScaffoldViewModel provides scaffoldViewModel
            ) { content(innerPadding) }
        }
    )
}