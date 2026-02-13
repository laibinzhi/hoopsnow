package com.hoopsnow.nba.core.data

import hoopsnow.shared.generated.resources.Res
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Provides team logo URLs by matching team full names.
 */
object TeamLogoProvider {
    private val logoMap: Map<String, String> by lazy { parseTeamsJson() }

    fun getLogoUrl(teamFullName: String): String? = logoMap[teamFullName]

    fun getAllLogos(): Map<String, String> = logoMap

    @OptIn(ExperimentalResourceApi::class)
    private fun parseTeamsJson(): Map<String, String> {
        return try {
            val jsonText = runBlocking {
                Res.readBytes("files/teams.json").decodeToString()
            }
            val root = Json.parseToJsonElement(jsonText).jsonObject
            val teams = root["sports"]?.jsonArray
                ?.firstOrNull()?.jsonObject
                ?.get("leagues")?.jsonArray
                ?.firstOrNull()?.jsonObject
                ?.get("teams")?.jsonArray
                ?: return emptyMap()

            buildMap {
                for (teamItem in teams) {
                    val team = teamItem.jsonObject["team"]?.jsonObject ?: continue
                    val displayName = team["displayName"]?.jsonPrimitive?.contentOrNull ?: continue
                    val logos = team["logos"]?.jsonArray ?: continue

                    val defaultLogoHref = logos.firstNotNullOfOrNull { logoItem ->
                        val logoObj = logoItem.jsonObject
                        val rel = logoObj["rel"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }.orEmpty()
                        if ("default" in rel) logoObj["href"]?.jsonPrimitive?.contentOrNull else null
                    }

                    if (defaultLogoHref != null) {
                        put(displayName, defaultLogoHref)
                    }
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
