package com.hoopsnow.nba

import android.app.Application
import com.hoopsnow.nba.di.platformModule
import com.hoopsnow.nba.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HoopsNowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HoopsNowApplication)
            modules(sharedModule, platformModule())
        }
    }
}
