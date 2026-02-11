package com.hoopsnow.nba.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API response wrapper with pagination metadata
 */
@Serializable
data class ApiResponse<T>(
    val data: T,
    val meta: Meta? = null,
)

@Serializable
data class Meta(
    @SerialName("total_pages")
    val totalPages: Int = 0,
    @SerialName("current_page")
    val currentPage: Int = 0,
    @SerialName("next_page")
    val nextPage: Int? = null,
    @SerialName("per_page")
    val perPage: Int = 0,
    @SerialName("total_count")
    val totalCount: Int = 0,
    @SerialName("next_cursor")
    val nextCursor: Int? = null,
)
