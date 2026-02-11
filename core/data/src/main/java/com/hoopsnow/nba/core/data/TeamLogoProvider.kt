package com.hoopsnow.nba.core.data

import android.content.Context
import org.json.JSONObject

/**
 * Provides team logo URLs by matching team full names.
 */
interface TeamLogoProvider {
    fun getLogoUrl(teamFullName: String): String?
    fun getAllLogos(): Map<String, String>
}

internal class TeamLogoProviderImpl(
    private val context: Context,
) : TeamLogoProvider {

    private val logoMap: Map<String, String> by lazy { parseTeamsJson() }

    override fun getLogoUrl(teamFullName: String): String? = logoMap[teamFullName]

    override fun getAllLogos(): Map<String, String> = logoMap

    private fun parseTeamsJson(): Map<String, String> {
        return try {
            val json = context.assets.open("teams.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val teams = root
                .getJSONArray("sports").getJSONObject(0)
                .getJSONArray("leagues").getJSONObject(0)
                .getJSONArray("teams")

            buildMap {
                for (i in 0 until teams.length()) {
                    val team = teams.getJSONObject(i).getJSONObject("team")
                    val displayName = team.getString("displayName")
                    val logos = team.getJSONArray("logos")

                    for (j in 0 until logos.length()) {
                        val logo = logos.getJSONObject(j)
                        val rel = logo.getJSONArray("rel")
                        val relList = (0 until rel.length()).map { rel.getString(it) }
                        if ("default" in relList) {
                            put(displayName, logo.getString("href"))
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
