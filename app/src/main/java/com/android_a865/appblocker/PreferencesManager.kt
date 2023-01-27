package com.android_a865.appblocker

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PreferencesManager {
    companion object {
        private const val LIST_KEY = "key"

        fun writeData(context: Context, apps: List<App>) {
            var data = ""
            apps.forEach {
                data += it.packageName + "/"
            }

            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putString(LIST_KEY, data)
            editor.apply()
        }

        fun readData(context: Context): List<String> {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val data = pref?.getString(LIST_KEY, "") ?: ""
            return data.split("/")
        }

    }

}