package com.cyber.restory

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App: Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this


    }

}