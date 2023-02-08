package com.android_a865.appblocker.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.usage.UsageEvents
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService: AccessibilityService() {


    override fun onCreate() {
        Log.d("app_running", "access is working")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d("app_running", "access is stopped")
        super.onCreate()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("app_running", "event happened")

        event?.let {
            Log.d("app_running", it.packageName.toString())
        }

    }

    override fun onInterrupt() {}


    override fun onServiceConnected() {
        Log.d("app_running", "Accessibility Started")

        serviceInfo.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.

            eventTypes = AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT or
                    AccessibilityEvent.TYPE_VIEW_LONG_CLICKED
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

    }

}