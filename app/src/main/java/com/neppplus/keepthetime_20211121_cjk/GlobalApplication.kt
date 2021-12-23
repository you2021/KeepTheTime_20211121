package com.neppplus.keepthetime_20211121_cjk

import android.app.Application
import com.kakao.sdk.common.KakaoSdk


class GlobalApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "4219905f4126eef9b31d5f2c82e8f437")

        // 네이버 지도 - manifests 또는 이곳 중 한곳에만 적으면 됨
//        NaverMapSdk.getInstance(this).client = NaverCloudPlatformClient("YOUR_CLIENT_ID_HERE")

    }
}