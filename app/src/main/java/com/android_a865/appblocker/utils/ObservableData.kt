package com.android_a865.appblocker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android_a865.appblocker.models.App

class ObservableData(apps: ArrayList<App>) {


    private val installedApps = MutableLiveData<ArrayList<App>>(ArrayList())

    var apps
        get() = installedApps.value!!
        set(value) {
            installedApps.value = value
        }
}