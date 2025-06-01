package songbird.apollo.presentation.ui.screens.scaffold

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun BottomNavBar(
    navController: NavController,
    tabs: ImmutableList<AppTab>
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentGraphDestination = currentBackStackEntry?.destination?.hierarchy?.first {
            it is NavGraph
        }
        val graphClassSimpleName = currentGraphDestination?.route
            ?.substringAfterLast('.')

        val currentTab = tabs.firstOrNull { it.graph::class.simpleName == graphClassSimpleName }
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = {
                    if (currentTab != null) {
                        navController.navigate(tab.graph) {
                            popUpTo(currentTab.graph) {
                                inclusive = true
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null // TODO: Добавить описание
                    )
                },
                label = {
                    Text(stringResource(tab.labelRes))
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    }
}