package com.cyber.restory

import android.app.Application
import com.cyber.restory.data.api.URL.KAKAO_API
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App: Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initKakaoMap()
    }

    private fun initKakaoMap() {
        KakaoMapSdk.init(this, KAKAO_API)
    }

}