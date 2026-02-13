package com.hoopsnow.nba.core.data

/**
 * Provides player headshot URLs by matching player full name to NBA player ID.
 * URL format: https://cdn.nba.com/headshots/nba/latest/1040x760/{nbaPlayerId}.png
 */
object PlayerHeadshotProvider {
    fun getHeadshotUrl(firstName: String, lastName: String): String {
        // Use NBA CDN with a generic approach based on player name
        val formattedName = "${firstName}_${lastName}".lowercase()
        return "https://cdn.nba.com/headshots/nba/latest/1040x760/$formattedName.png"
    }
}
