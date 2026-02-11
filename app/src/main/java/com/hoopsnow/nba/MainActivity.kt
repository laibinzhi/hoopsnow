package com.hoopsnow.nba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.hoopsnow.nba.core.data.PlayerHeadshotProvider
import com.hoopsnow.nba.core.data.TeamLogoProvider
import com.hoopsnow.nba.core.designsystem.component.LocalPlayerHeadshot
import com.hoopsnow.nba.core.designsystem.component.LocalTeamLogos
import com.hoopsnow.nba.core.designsystem.theme.HoopsNowTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var teamLogoProvider: TeamLogoProvider

    @Inject
    lateinit var playerHeadshotProvider: PlayerHeadshotProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置状态栏为深色背景 + 浅色图标/文字
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            HoopsNowTheme {
                CompositionLocalProvider(
                    LocalTeamLogos provides teamLogoProvider.getAllLogos(),
                    LocalPlayerHeadshot provides playerHeadshotProvider::getHeadshotUrl,
                ) {
                    HoopsNowApp()
                }
            }
        }
    }
}
