package com.hoopsnow.nba.core.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * A beautiful HTTP logging interceptor that formats request/response logs
 * in a visually appealing way for easier debugging.
 */
class PrettyHttpLogger(
    private val tag: String = "HttpLog",
    private val logLevel: LogLevel = LogLevel.BODY,
) : Interceptor {

    enum class LogLevel {
        NONE,       // No logs
        BASIC,      // Request/Response line only
        HEADERS,    // + Headers
        BODY,       // + Body (formatted JSON)
    }

    companion object {
        private const val TOP_BORDER = "┌──────────────────────────────────────────────��─────────────────────────"
        private const val BOTTOM_BORDER = "└────────────────────────────────────────────────────────────────────────"
        private const val MIDDLE_BORDER = "├────────────────────────────────────────────────────────────────────────"
        private const val SIDE_BORDER = "│ "
        private const val EMPTY_LINE = "│"

        private const val MAX_BODY_LOG_SIZE = 1024 * 1024 // 1MB
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (logLevel == LogLevel.NONE) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
        val requestStartTime = System.nanoTime()

        // Log Request
        logRequest(request)

        // Execute request
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logError(request.url.toString(), e)
            throw e
        }

        val requestDuration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStartTime)

        // Log Response
        return logResponse(response, requestDuration)
    }

    private fun logRequest(request: okhttp3.Request) {
        val builder = StringBuilder()

        builder.appendLine()
        builder.appendLine(TOP_BORDER)
        builder.appendLine("$SIDE_BORDER🚀 REQUEST")
        builder.appendLine(MIDDLE_BORDER)

        // Method & URL
        builder.appendLine("$SIDE_BORDER${request.method} ${request.url}")

        if (logLevel.ordinal >= LogLevel.HEADERS.ordinal) {
            builder.appendLine(MIDDLE_BORDER)
            builder.appendLine("$SIDE_BORDER📋 Headers:")

            if (request.headers.size == 0) {
                builder.appendLine("$SIDE_BORDER   (none)")
            } else {
                request.headers.forEach { (name, value) ->
                    val displayValue = if (name.equals("Authorization", ignoreCase = true)) {
                        maskSensitiveValue(value)
                    } else {
                        value
                    }
                    builder.appendLine("$SIDE_BORDER   $name: $displayValue")
                }
            }
        }

        if (logLevel == LogLevel.BODY) {
            request.body?.let { body ->
                builder.appendLine(MIDDLE_BORDER)
                builder.appendLine("$SIDE_BORDER📦 Body:")

                val buffer = Buffer()
                body.writeTo(buffer)
                val bodyString = buffer.readString(Charset.forName("UTF-8"))

                if (bodyString.isNotEmpty()) {
                    formatJson(bodyString).lines().forEach { line ->
                        builder.appendLine("$SIDE_BORDER   $line")
                    }
                } else {
                    builder.appendLine("$SIDE_BORDER   (empty)")
                }
            }
        }

        builder.appendLine(BOTTOM_BORDER)

        Log.d(tag, builder.toString())
    }

    private fun logResponse(response: Response, durationMs: Long): Response {
        val builder = StringBuilder()

        builder.appendLine()
        builder.appendLine(TOP_BORDER)

        // Status indicator
        val statusEmoji = when {
            response.isSuccessful -> "✅"
            response.code in 400..499 -> "⚠️"
            else -> "❌"
        }

        builder.appendLine("$SIDE_BORDER$statusEmoji RESPONSE [${response.code} ${response.message}]")
        builder.appendLine(MIDDLE_BORDER)

        // URL & Duration
        builder.appendLine("$SIDE_BORDER${response.request.method} ${response.request.url}")
        builder.appendLine("$SIDE_BORDER⏱️ Duration: ${durationMs}ms")

        if (logLevel.ordinal >= LogLevel.HEADERS.ordinal) {
            builder.appendLine(MIDDLE_BORDER)
            builder.appendLine("$SIDE_BORDER📋 Headers:")

            if (response.headers.size == 0) {
                builder.appendLine("$SIDE_BORDER   (none)")
            } else {
                response.headers.forEach { (name, value) ->
                    builder.appendLine("$SIDE_BORDER   $name: $value")
                }
            }
        }

        var newResponse = response
        if (logLevel == LogLevel.BODY) {
            val responseBody = response.body
            if (responseBody != null) {
                val contentLength = responseBody.contentLength()
                val contentType = responseBody.contentType()

                builder.appendLine(MIDDLE_BORDER)

                if (contentLength > MAX_BODY_LOG_SIZE) {
                    builder.appendLine("$SIDE_BORDER📦 Body: (too large to log: ${contentLength / 1024}KB)")
                } else {
                    builder.appendLine("$SIDE_BORDER📦 Body:")

                    val source = responseBody.source()
                    source.request(Long.MAX_VALUE)
                    val buffer = source.buffer

                    val bodyString = buffer.clone().readString(Charset.forName("UTF-8"))

                    if (bodyString.isNotEmpty()) {
                        formatJson(bodyString).lines().forEach { line ->
                            builder.appendLine("$SIDE_BORDER   $line")
                        }
                    } else {
                        builder.appendLine("$SIDE_BORDER   (empty)")
                    }

                    // Recreate response body since we consumed it
                    newResponse = response.newBuilder()
                        .body(bodyString.toResponseBody(contentType))
                        .build()
                }
            }
        }

        builder.appendLine(BOTTOM_BORDER)

        Log.d(tag, builder.toString())

        return newResponse
    }

    private fun logError(url: String, error: Exception) {
        val builder = StringBuilder()

        builder.appendLine()
        builder.appendLine(TOP_BORDER)
        builder.appendLine("$SIDE_BORDER❌ HTTP ERROR")
        builder.appendLine(MIDDLE_BORDER)
        builder.appendLine("$SIDE_BORDER🔗 URL: $url")
        builder.appendLine("$SIDE_BORDER💥 Error: ${error.javaClass.simpleName}")
        builder.appendLine("$SIDE_BORDER📝 Message: ${error.message}")
        builder.appendLine(BOTTOM_BORDER)

        Log.e(tag, builder.toString())
    }

    private fun formatJson(json: String): String {
        return try {
            when {
                json.trimStart().startsWith("{") -> {
                    JSONObject(json).toString(2)
                }
                json.trimStart().startsWith("[") -> {
                    JSONArray(json).toString(2)
                }
                else -> json
            }
        } catch (e: Exception) {
            json
        }
    }

    private fun maskSensitiveValue(value: String): String {
        return if (value.length > 8) {
            "${value.take(4)}****${value.takeLast(4)}"
        } else {
            "****"
        }
    }
}
