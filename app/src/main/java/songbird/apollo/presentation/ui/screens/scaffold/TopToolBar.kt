package songbird.apollo.presentation.ui.screens.scaffold

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class NavigateUpAction {
    data object Hidden : NavigateUpAction()
    data class Visible(
        val onClick: () -> Unit
    ): NavigateUpAction()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolBar(
    @StringRes title: Int,
    navigateUp: NavigateUpAction
) {
    MediumTopAppBar(
        title = { Text(text = stringResource(title)) },
        navigationIcon = {
            if (navigateUp is NavigateUpAction.Visible) {
                IconButton(onClick = navigateUp.onClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        // TODO: Добавить описание
                        contentDescription = null
                    )
                }
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}