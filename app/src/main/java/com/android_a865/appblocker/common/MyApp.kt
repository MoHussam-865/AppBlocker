package com.android_a865.appblocker.common

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.services.BackgroundManager


class MyApp: Application() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        BackgroundManager.instance?.startService(applicationContext)
    }
}