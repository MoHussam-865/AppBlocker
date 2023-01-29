package com.android_a865.appblocker.services

import android.content.Context
import android.preference.PreferenceManager
import com.android_a865.appblocker.models.App

class PreferencesManager {
    companion object {
        const val LOCKED_APPS = "locked_apps"
        const val ALLOWED_APPS = "allowed_apps"
        const val END_TIME = "end_time"

        fun setLockedApps(context: Context, apps: List<App>) {
            var data = ""
            apps.forEach {
                data += it.packageName + "/"
            }

            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putString(LOCKED_APPS, data)
            editor.apply()
        }

        fun getLockedApps(context: Context): List<String> {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val data = pref?.getString(LOCKED_APPS, "") ?: ""
            return data.split("/")
        }

        fun setAllowedApps(context: Context, apps: List<App>) {
            var data = ""
            apps.forEach {
                data += it.packageName + "/"
            }

            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putString(ALLOWED_APPS, data)
            editor.apply()
        }

        fun getAllowedApps(context: Context): List<String> {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val data = pref?.getString(ALLOWED_APPS, "") ?: ""
            return data.split("/")
        }

        fun setEndTime(context: Context, time: Long) {
            PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putLong(END_TIME, time)
                .apply()
        }

        fun getEndTime(context: Context) = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getLong(END_TIME, System.currentTimeMillis())



    }

}