package com.hoopsnow.nba.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val position: String,
    val team: Team?,
)
