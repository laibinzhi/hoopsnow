package com.hoopsnow.nba.core.data

/**
 * Provides team logo URLs by matching team full names.
 */
object TeamLogoProvider {
    private val logoMap: Map<String, String> = mapOf(
        "Atlanta Hawks" to "https://a.espncdn.com/i/teamlogos/nba/500/atl.png",
        "Boston Celtics" to "https://a.espncdn.com/i/teamlogos/nba/500/bos.png",
        "Brooklyn Nets" to "https://a.espncdn.com/i/teamlogos/nba/500/bkn.png",
        "Charlotte Hornets" to "https://a.espncdn.com/i/teamlogos/nba/500/cha.png",
        "Chicago Bulls" to "https://a.espncdn.com/i/teamlogos/nba/500/chi.png",
        "Cleveland Cavaliers" to "https://a.espncdn.com/i/teamlogos/nba/500/cle.png",
        "Dallas Mavericks" to "https://a.espncdn.com/i/teamlogos/nba/500/dal.png",
        "Denver Nuggets" to "https://a.espncdn.com/i/teamlogos/nba/500/den.png",
        "Detroit Pistons" to "https://a.espncdn.com/i/teamlogos/nba/500/det.png",
        "Golden State Warriors" to "https://a.espncdn.com/i/teamlogos/nba/500/gs.png",
        "Houston Rockets" to "https://a.espncdn.com/i/teamlogos/nba/500/hou.png",
        "Indiana Pacers" to "https://a.espncdn.com/i/teamlogos/nba/500/ind.png",
        "LA Clippers" to "https://a.espncdn.com/i/teamlogos/nba/500/lac.png",
        "Los Angeles Lakers" to "https://a.espncdn.com/i/teamlogos/nba/500/lal.png",
        "Memphis Grizzlies" to "https://a.espncdn.com/i/teamlogos/nba/500/mem.png",
        "Miami Heat" to "https://a.espncdn.com/i/teamlogos/nba/500/mia.png",
        "Milwaukee Bucks" to "https://a.espncdn.com/i/teamlogos/nba/500/mil.png",
        "Minnesota Timberwolves" to "https://a.espncdn.com/i/teamlogos/nba/500/min.png",
        "New Orleans Pelicans" to "https://a.espncdn.com/i/teamlogos/nba/500/no.png",
        "New York Knicks" to "https://a.espncdn.com/i/teamlogos/nba/500/ny.png",
        "Oklahoma City Thunder" to "https://a.espncdn.com/i/teamlogos/nba/500/okc.png",
        "Orlando Magic" to "https://a.espncdn.com/i/teamlogos/nba/500/orl.png",
        "Philadelphia 76ers" to "https://a.espncdn.com/i/teamlogos/nba/500/phi.png",
        "Phoenix Suns" to "https://a.espncdn.com/i/teamlogos/nba/500/phx.png",
        "Portland Trail Blazers" to "https://a.espncdn.com/i/teamlogos/nba/500/por.png",
        "Sacramento Kings" to "https://a.espncdn.com/i/teamlogos/nba/500/sac.png",
        "San Antonio Spurs" to "https://a.espncdn.com/i/teamlogos/nba/500/sa.png",
        "Toronto Raptors" to "https://a.espncdn.com/i/teamlogos/nba/500/tor.png",
        "Utah Jazz" to "https://a.espncdn.com/i/teamlogos/nba/500/uta.png",
        "Washington Wizards" to "https://a.espncdn.com/i/teamlogos/nba/500/wsh.png",
    )

    fun getLogoUrl(teamFullName: String): String? = logoMap[teamFullName]

    fun getAllLogos(): Map<String, String> = logoMap
}
