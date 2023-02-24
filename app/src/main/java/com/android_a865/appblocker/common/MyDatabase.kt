package com.android_a865.appblocker.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android_a865.appblocker.common.MyDatabase.Companion.DATABASE_VERSION
import com.android_a865.appblocker.feature_home.data.PkgDao
import com.android_a865.appblocker.feature_home.data.PkgEntity


@Database(
    entities = [
        PkgEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)

abstract class MyDatabase: RoomDatabase() {

    abstract fun getPkgs(): PkgDao

    companion object {
        // Room Database
        const val DATABASE_NAME = "AppBlocker.db"
        const val DATABASE_VERSION = 1
    }
}