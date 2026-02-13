package com.hoopsnow.nba.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.hoopsnow.nba.ui.navigation.FavoritesTab
import com.hoopsnow.nba.ui.navigation.GamesTab
import com.hoopsnow.nba.ui.navigation.PlayersTab
import com.hoopsnow.nba.ui.navigation.TeamsTab
import com.hoopsnow.nba.ui.theme.*

@Composable
fun HoopsNowApp() {
    HoopsNowTheme {
        TabNavigator(GamesTab) {
            Scaffold(
                containerColor = Slate950,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    Column {
                        HorizontalDivider(color = Slate800, thickness = 1.dp)
                        NavigationBar(
                            containerColor = Slate900,
                            windowInsets = WindowInsets(0, 0, 0, 0),
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                        ) {
                            TabNavigationItem(GamesTab)
                            TabNavigationItem(TeamsTab)
                            TabNavigationItem(PlayersTab)
                            TabNavigationItem(FavoritesTab)
                        }
                    }
                },
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val selected = tabNavigator.current.key == tab.key

    NavigationBarItem(
        selected = selected,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { painter ->
                Icon(
                    painter = painter,
                    contentDescription = tab.options.title,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        label = {
            Text(
                text = tab.options.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Blue400,
            selectedTextColor = Blue400,
            unselectedIconColor = Slate600,
            unselectedTextColor = Slate600,
            indicatorColor = Slate900,
        ),
    )
}
