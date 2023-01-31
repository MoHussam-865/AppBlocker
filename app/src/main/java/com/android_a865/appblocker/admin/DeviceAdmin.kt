package com.android_a865.appblocker.admin


import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment


class DeviceAdminSample : PreferenceActivity() {


    var mDPM: DevicePolicyManager? = null
    var mDeviceAdminSample: ComponentName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prepare to work with the DPM
        mDPM = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mDeviceAdminSample = ComponentName(
            this,
            DeviceAdminSample.DeviceAdminSampleReceiver::class.java
        )
    }


    class AdminSampleFragment : PreferenceFragment(),
        OnPreferenceChangeListener, OnPreferenceClickListener {

        @Deprecated("Deprecated in Java", ReplaceWith("true"))
        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            return false
        }

        @Deprecated("Deprecated in Java", ReplaceWith("true"))
        override fun onPreferenceClick(preference: Preference?): Boolean {
            return false
        }
    }

    class DeviceAdminSampleReceiver : DeviceAdminReceiver() {
    }



}