package com.android_a865.appblocker.services

import android.accessibilityservice.AccessibilityService
import android.app.usage.UsageEvents
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService: AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName as String
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        Log.d("access PackageName", packageName)
        Log.d("access parc", event.text.toString())

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            // event.text.toString()
            Log.d("access", "TYPE_VIEW_TEXT_CHANGED")
        }
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

}