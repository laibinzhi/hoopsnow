package com.hoopsnow.nba.core.data

import hoopsnow.shared.generated.resources.Res
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Provides player headshot URLs by matching player full name to NBA player ID.
 * URL format: https://cdn.nba.com/headshots/nba/latest/1040x760/{nbaPlayerId}.png
 */
object PlayerHeadshotProvider {
    private val playerIdMap: Map<String, Int> by lazy { parsePlayersJson() }

    fun getHeadshotUrl(firstName: String, lastName: String): String? {
        val fullName = "$firstName $lastName".trim()
        val nbaPlayerId = playerIdMap[fullName] ?: return null
        return "https://cdn.nba.com/headshots/nba/latest/1040x760/$nbaPlayerId.png"
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun parsePlayersJson(): Map<String, Int> {
        return try {
            val jsonText = runBlocking {
                Res.readBytes("files/nba_players_name_id.json").decodeToString()
            }
            val players = Json.parseToJsonElement(jsonText).jsonArray
            buildMap {
                for (player in players) {
                    val playerObj = player.jsonObject
                    val name = playerObj["name"]?.jsonPrimitive?.contentOrNull ?: continue
                    val id = playerObj["id"]?.jsonPrimitive?.intOrNull ?: continue
                    put(name, id)
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
