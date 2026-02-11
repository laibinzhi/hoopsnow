package com.hoopsnow.nba.core.common.result

/**
 * Custom exceptions for NBA app
 */
sealed class NbaException(message: String) : Exception(message) {
    /**
     * Rate limit exceeded (HTTP 429)
     */
    class RateLimitException(message: String = "Too many requests, please try again later") : NbaException(message)
}
