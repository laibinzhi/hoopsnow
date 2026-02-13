package com.hoopsnow.nba.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Int,
    val conference: String,
    val division: String,
    val city: String,
    val name: String,
    val fullName: String,
    val abbreviation: String,
)
