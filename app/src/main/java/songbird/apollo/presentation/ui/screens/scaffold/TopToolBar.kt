package songbird.apollo.presentation.ui.screens.scaffold

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import songbird.apollo.presentation.ui.screens.LocalNavController

sealed class NavigateAction {
    data object Disable : NavigateAction()
    data class Enable(
        val onClick: () -> Unit
    ) : NavigateAction()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolBar(
    @StringRes title: Int,
    navigateUp: Boolean = false,
    showSettingsButton: Boolean = true,
    toSettingsScreen: NavigateAction.Enable,
) {

    val navController = LocalNavController.current

    Column {
        MediumTopAppBar(
            title = {
                Text(
                    text = stringResource(title),
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }, navigationIcon = {
                if (navigateUp) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            // TODO: Добавить описание
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground
            ), actions = {
                if (showSettingsButton) {
                    IconButton(
                        onClick = toSettingsScreen.onClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            // TODO: Добавить описание
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
    }
}