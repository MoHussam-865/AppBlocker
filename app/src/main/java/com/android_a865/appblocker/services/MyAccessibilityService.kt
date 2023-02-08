package com.android_a865.appblocker.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.usage.UsageEvents
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.R
import com.android_a865.appblocker.utils.getForegroundApp
import com.android_a865.appblocker.utils.getForegroundAppName
import com.android_a865.appblocker.utils.killCurrentProcess

class MyAccessibilityService: AccessibilityService() {
    
    private val TAG = "app_running"
    private val someArray = ArrayList<String>()

    override fun onCreate() {
        Log.d(TAG, "access is working")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d(TAG, "access is stopped")
        super.onCreate()
    }

    @RequiresApi(33)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //Log.d(TAG, "event happened")

        if (event == null){
            Log.d(TAG, "event is null")
        }
        event?.let {
            if (it.text.isEmpty()) return

            //Log.d(TAG, "source: ${it.source?.uniqueId}")
            Log.d(TAG, "${it.source}")

        }
    }

    override fun onInterrupt() {}




    override fun onServiceConnected() {
        Log.d(TAG, "Accessibility Started")

        val info = AccessibilityServiceInfo()
        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.

            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            // If you only want this service to work with specific applications, set their
            // package names here. Otherwise, when the service is activated, it will listen
            // to events from all applications.
            packageNames = arrayOf("com.android.settings")

            // Set the type of feedback your service will provide.
            feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN

            // Default services are invoked only if no package-specific ones are present
            // for the type of AccessibilityEvent generated. This service *is*
            // application-specific, so the flag isn't necessary. If this was a
            // general-purpose service, it would be worth considering setting the
            // DEFAULT flag.

            // flags = AccessibilityServiceInfo.DEFAULT;

            notificationTimeout = 100
        }
        this.serviceInfo = info
    }



}