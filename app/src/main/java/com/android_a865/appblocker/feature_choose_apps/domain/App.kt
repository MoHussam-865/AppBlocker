package com.android_a865.appblocker.feature_choose_apps.domain

import android.graphics.drawable.Drawable

data class App(
    val icon: Drawable,
    val name: String?,
    val packageName: String,
    var selected: Boolean = false,
    var active: Boolean = false,
)
