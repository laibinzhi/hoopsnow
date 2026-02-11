package com.hoopsnow.nba.core.data

import android.content.Context
import org.json.JSONArray

/**
 * Provides player headshot URLs by matching player full name to NBA player ID.
 * URL format: https://cdn.nba.com/headshots/nba/latest/1040x760/{nbaPlayerId}.png
 */
interface PlayerHeadshotProvider {
    fun getHeadshotUrl(firstName: String, lastName: String): String?
}

internal class PlayerHeadshotProviderImpl(
    private val context: Context,
) : PlayerHeadshotProvider {

    private val playerIdMap: Map<String, Int> by lazy { parsePlayersJson() }

    override fun getHeadshotUrl(firstName: String, lastName: String): String? {
        val fullName = "$firstName $lastName"
        val nbaPlayerId = playerIdMap[fullName] ?: return null
        return "https://cdn.nba.com/headshots/nba/latest/1040x760/$nbaPlayerId.png"
    }

    private fun parsePlayersJson(): Map<String, Int> {
        return try {
            val json = context.assets.open("nba_players_name_id.json").bufferedReader().use { it.readText() }
            val players = JSONArray(json)

            buildMap {
                for (i in 0 until players.length()) {
                    val player = players.getJSONObject(i)
                    val name = player.getString("name")
                    val id = player.getInt("id")
                    put(name, id)
                }
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
