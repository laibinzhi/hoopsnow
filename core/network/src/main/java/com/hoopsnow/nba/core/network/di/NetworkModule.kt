package com.hoopsnow.nba.core.network.di

import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.core.network.interceptor.PrettyHttpLogger
import com.hoopsnow.nba.core.network.retrofit.ApiKeyInterceptor
import com.hoopsnow.nba.core.network.retrofit.RetrofitNbaNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {

    @Binds
    fun bindsNbaNetworkDataSource(
        impl: RetrofitNbaNetwork,
    ): NbaNetworkDataSource

    companion object {

        @Provides
        @Singleton
        fun providesNetworkJson(): Json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        @Provides
        @Singleton
        fun okHttpCallFactory(): Call.Factory = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(
                PrettyHttpLogger(
                    tag = "HoopsNow-HTTP",
                    logLevel = PrettyHttpLogger.LogLevel.BODY,
                )
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
