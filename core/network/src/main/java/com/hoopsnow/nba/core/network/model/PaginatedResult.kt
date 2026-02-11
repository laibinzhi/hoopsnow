package com.hoopsnow.nba.core.network.model

/**
 * Wrapper for paginated API results with cursor-based pagination
 */
data class PaginatedResult<T>(
    val data: List<T>,
    val nextCursor: Int?,
) {
    val hasMore: Boolean get() = nextCursor != null
}
