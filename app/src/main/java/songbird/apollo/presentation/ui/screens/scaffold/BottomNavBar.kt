package songbird.apollo.presentation.ui.screens.scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentGraphDestination = currentBackStackEntry?.destination?.hierarchy?.first {
                it is NavGraph
            }

            val graphRoute = currentGraphDestination?.route

            val currentTab = tabs.firstOrNull {it.name == graphRoute }
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
                        Text(
                            text = stringResource(tab.labelRes),
                            color = if (currentTab == tab)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
}