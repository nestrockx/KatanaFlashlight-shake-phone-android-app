package com.wegielek.katanaflashlight

import android.app.Application
import com.wegielek.katanaflashlight.di.dataModule
import com.wegielek.katanaflashlight.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(presentationModule, dataModule))
        }
    }
}
