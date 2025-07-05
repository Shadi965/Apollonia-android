package songbird.apollo.presentation.ui.screens.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import songbird.apollo.presentation.ui.screens.LocalNavController
import songbird.apollo.presentation.ui.screens.SettingsScreenRoute
import songbird.apollo.presentation.ui.screens.player.LocalPlayerEntryViewModel
import songbird.apollo.presentation.ui.screens.player.PlayerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val navController = LocalNavController.current

    val scaffoldViewModel: ScaffoldViewModel = hiltViewModel()
    val playerEntryViewModel = LocalPlayerEntryViewModel.current

    val uiState by scaffoldViewModel.uiState.collectAsState()
    val playerState by playerEntryViewModel.playerVisible.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    playerEntryViewModel.hidePlayer(!uiState.showBottomBar)

    CompositionLocalProvider(
        LocalScaffoldViewModel provides scaffoldViewModel,
    ) {
        Box(modifier = modifier) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                    playerEntryViewModel.setBottomPadding(innerPadding.calculateBottomPadding())

                    val topPadding =
                        if (uiState.showTopBar)
                            innerPadding.calculateTopPadding()
                        else 0.dp
                    val bottomPadding =
                        if (uiState.showBottomBar)
                            if (playerState.isVisible)
                                innerPadding.calculateBottomPadding() + playerState.collapseHeight
                            else
                                innerPadding.calculateBottomPadding()
                        else 0.dp

                    content(
                        PaddingValues(
                            top = topPadding,
                            bottom = bottomPadding,
                        )
                    )
                }
            )

            if (playerState.isVisible) {
                PlayerScreen(modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}