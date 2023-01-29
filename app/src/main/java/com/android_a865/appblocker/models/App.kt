package com.android_a865.appblocker.models

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon

data class App(
    val icon: Drawable,
    val name: String?,
    val packageName: String?,
    var selected: Boolean = false,
)
