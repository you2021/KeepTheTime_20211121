package com.neppplus.keepthetime_20211121

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "4219905f4126eef9b31d5f2c82e8f437")

    }
}