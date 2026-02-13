package com.hoopsnow.nba.core.common

/**
 * Custom exceptions for NBA app
 */
sealed class NbaException(message: String) : Exception(message) {
    /**
     * Rate limit exceeded (HTTP 429)
     */
    class RateLimitException(message: String = "Too many requests, please try again later") : NbaException(message)

    /**
     * Network error
     */
    class NetworkException(message: String = "Network error occurred") : NbaException(message)

    /**
     * Unknown error
     */
    class UnknownException(message: String = "An unknown error occurred") : NbaException(message)
}
