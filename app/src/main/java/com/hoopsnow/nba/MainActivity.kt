package com.hoopsnow.nba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.hoopsnow.nba.core.data.PlayerHeadshotProvider
import com.hoopsnow.nba.core.data.TeamLogoProvider
import com.hoopsnow.nba.ui.HoopsNowApp
import com.hoopsnow.nba.ui.component.LocalPlayerHeadshot
import com.hoopsnow.nba.ui.component.LocalTeamLogos

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            CompositionLocalProvider(
                LocalTeamLogos provides TeamLogoProvider.getAllLogos(),
                LocalPlayerHeadshot provides PlayerHeadshotProvider::getHeadshotUrl,
            ) {
                HoopsNowApp()
            }
        }
    }
}
