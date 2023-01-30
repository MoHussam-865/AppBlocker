package com.android_a865.appblocker.admin


import android.app.admin.DeviceAdminReceiver
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment




class DeviceAdminSample : PreferenceActivity() {


    class AdminSampleFragment : PreferenceFragment(),
        OnPreferenceChangeListener, OnPreferenceClickListener {
        @Deprecated("Deprecated in Java", ReplaceWith("true"))
        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            return true
        }

        @Deprecated("Deprecated in Java", ReplaceWith("true"))
        override fun onPreferenceClick(preference: Preference?): Boolean {
            return true
        }
    }

    class DeviceAdminSampleReceiver : DeviceAdminReceiver() {
    }



}