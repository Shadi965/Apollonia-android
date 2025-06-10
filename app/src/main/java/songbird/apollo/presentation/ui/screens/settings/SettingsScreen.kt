package songbird.apollo.presentation.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import songbird.apollo.R
import songbird.apollo.presentation.ui.screens.LocalNavController
import songbird.apollo.presentation.ui.screens.scaffold.ModifyScaffoldUi

@Composable
fun SettingsScreen() {
    val navController = LocalNavController.current

    ModifyScaffoldUi(
        topBarTitle = R.string.settings,
        showBottomBar = false,
        showNavigateUp = true,
        showSettingsButton = false,
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.settings))
    }
}