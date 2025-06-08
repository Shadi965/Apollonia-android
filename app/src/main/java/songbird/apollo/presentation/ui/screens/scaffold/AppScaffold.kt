package songbird.apollo.presentation.ui.screens.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import songbird.apollo.presentation.ui.screens.LocalNavController

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val navController = LocalNavController.current

    val scaffoldViewModel: ScaffoldViewModel = hiltViewModel()
    val uiState by scaffoldViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            if (uiState.showTopBar) {
                TopToolBar(
                    navigateUp = uiState.navigateUp,
                    title = uiState.topBarTitle
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