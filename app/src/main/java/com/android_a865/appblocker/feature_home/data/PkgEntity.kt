package com.android_a865.appblocker.feature_home.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Packages")
data class PkgEntity(
    @PrimaryKey
    val name: String,
    val time: Int,
    val apps: String = "",
    var isActive: Boolean = false,
)
