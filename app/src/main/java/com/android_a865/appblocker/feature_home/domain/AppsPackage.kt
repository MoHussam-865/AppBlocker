package com.android_a865.appblocker.feature_home.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppsPackage(
    val name: String,
    val time: Long,
    val apps: List<String>,
    val isActive: Boolean = false,
) : Parcelable
