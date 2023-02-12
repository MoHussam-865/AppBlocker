package com.android_a865.appblocker.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.R
import com.android_a865.appblocker.admin.MyDeviceAdminReceiver
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.utils.createMessage
import com.android_a865.appblocker.utils.isServiceRunning
import com.android_a865.appblocker.utils.killPackageIfRunning

class MyAccessibilityService : AccessibilityService() {

    private val TAG = "app_running"
    private lateinit var appName: String

    override fun onCreate() {
        appName = getString(R.string.app_name)
        Log.d(TAG, "access is working")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d(TAG, "access is stopped")
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //Log.d(TAG, "event happened")

        if (!isServiceRunning(
                this,
                ServiceAppLockJobIntent::class.java
            )
        ) {
            return
        }


        event?.let { myEvent ->
            when (myEvent.eventType) {
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                    Log.d(TAG, "TYPE_VIEW_LONG_CLICKED")
                    Log.d(TAG, "${myEvent.text}${myEvent.contentDescription}")
                    checkEvent(myEvent)
                }
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    Log.d(TAG, "TYPE_VIEW_CLICKED")
                    Log.d(TAG, "${myEvent.text}${myEvent.contentDescription}")
                    checkEvent(myEvent)
                }
                else -> {
                    //Log.d(TAG, "")
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun checkEvent(event: AccessibilityEvent) {
        Thread.sleep(100)
        event.apply {

            if (
                (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED &&
                        text.contains(appName) &&
                        contentDescription != appName) ||
                (eventType == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED &&
                        contentDescription.toString().contains(appName))
            ) {
                Log.d(TAG, "kill event")

                killPackageIfRunning(
                    this@MyAccessibilityService,
                    packageName.toString()
                )

                Toast.makeText(
                    this@MyAccessibilityService,
                    "Sorry you can't access app settings for now",
                    Toast.LENGTH_LONG
                ).show()

            }
        }

    }

    override fun onInterrupt() {}

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onServiceConnected() {
        Log.d(TAG, "Accessibility Started")

        val info = AccessibilityServiceInfo()
        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.

            //eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_VIEW_LONG_CLICKED
            // If you only want this service to work with specific applications, set their
            // package names here. Otherwise, when the service is activated, it will listen
            // to events from all applications.
            packageNames = arrayOf(
                "com.android.settings",
                "com.android.launcher",
            )

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