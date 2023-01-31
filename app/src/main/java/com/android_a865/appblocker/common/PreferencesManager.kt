package com.android_a865.appblocker.common

import android.content.Context
import android.preference.PreferenceManager
import com.android_a865.appblocker.models.App

class PreferencesManager {
    companion object {
        private const val LOCKED_APPS = "locked_apps"
        private const val ALLOWED_APPS = "allowed_apps"
        private const val END_TIME = "end_time"
        private const val LAST_TIME = "last_time"



        fun getLockedApps(context: Context): List<String> {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(LOCKED_APPS, "")?.split("/")
                ?.filter { it != "" } ?: emptyList()
        }



        fun getAllowedApps(context: Context): List<String> {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(ALLOWED_APPS, "")?.split("/")
                ?.filter { it != "" } ?: emptyList()
        }



        fun getEndTime(context: Context) = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getLong(END_TIME, System.currentTimeMillis())


        fun getLastTime(context: Context) = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getInt(LAST_TIME, 0)


        fun setEndTime(context: Context, time: Long) {
            PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putLong(END_TIME, time)
                .apply()
        }

        fun setAllowedApps(context: Context, apps: List<App>) {
            var data = ""
            apps.forEach {
                data += it.packageName + "/"
            }

            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(ALLOWED_APPS, data)
                .apply()
        }

        fun setLockedApps(context: Context, apps: List<App>) {
            var data = ""
            apps.forEach {
                data += it.packageName + "/"
            }

            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(LOCKED_APPS, data)
                .apply()
        }

        fun setLastTime(context: Context, time: Int) {
            PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putInt(LAST_TIME, time)
                .apply()
        }

        fun setupLockSettings(
            context: Context,
            endTime: Long,
            lockedApps: List<App>,
            allowedApps: List<App>,
            lastTime: Int
        ) {
            var dataLocked = ""
            lockedApps.forEach {
                dataLocked += it.packageName + "/"
            }

            var dataAllowed = ""
            allowedApps.forEach {
                dataAllowed += it.packageName + "/"
            }

            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(LOCKED_APPS, dataLocked)
                .putString(ALLOWED_APPS, dataAllowed)
                .putLong(END_TIME, endTime)
                .putInt(LAST_TIME, lastTime)
                .apply()

        }

    }

}