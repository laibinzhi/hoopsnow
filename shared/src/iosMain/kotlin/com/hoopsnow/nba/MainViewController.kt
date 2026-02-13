package com.hoopsnow.nba

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.hoopsnow.nba.core.data.PlayerHeadshotProvider
import com.hoopsnow.nba.core.data.TeamLogoProvider
import com.hoopsnow.nba.ui.HoopsNowApp
import com.hoopsnow.nba.ui.component.LocalPlayerHeadshot
import com.hoopsnow.nba.ui.component.LocalTeamLogos

fun MainViewController() = ComposeUIViewController {
    CompositionLocalProvider(
        LocalTeamLogos provides TeamLogoProvider.getAllLogos(),
        LocalPlayerHeadshot provides PlayerHeadshotProvider::getHeadshotUrl,
    ) {
        HoopsNowApp()
    }
}
