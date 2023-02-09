package com.android_a865.appblocker.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.R
import com.android_a865.appblocker.admin.MyDeviceAdminReceiver
import com.android_a865.appblocker.utils.createMessage
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

        /*if (!isServiceRunning(
                    this,
                    ServiceAppLockJobIntent::class.java
                )
            ){ return }*/


        event?.let { myEvent ->


            when (myEvent.eventType) {
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                    Log.d(TAG, "TYPE_VIEW_LONG_CLICKED")
                    Log.d(TAG, "${myEvent.text}")
                    Log.d(TAG, "${myEvent.contentDescription}")
                    checkEvent(myEvent)
                }
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    Log.d(TAG, "TYPE_VIEW_CLICKED")
                    Log.d(TAG, "${myEvent.text}")
                    Log.d(TAG, "${myEvent.contentDescription}")
                    checkEvent(myEvent)
                }
                AccessibilityEvent.WINDOWS_CHANGE_TITLE -> {
                    Log.d(TAG, "WINDOWS_CHANGE_TITLE")
                }
                AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED -> {
                    Log.d(TAG, "TYPE_VIEW_CONTEXT_CLICKED")
                }
                AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> {
                    Log.d(TAG, "TYPE_VIEW_ACCESSIBILITY_FOCUSED")
                }
                AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                    Log.d(TAG, "TYPE_WINDOWS_CHANGED")
                }

                AccessibilityEvent.TYPE_ANNOUNCEMENT -> {
                    Log.d(TAG, "TYPE_ANNOUNCEMENT")
                }
                AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT -> {
                    Log.d(TAG, "TYPE_ASSIST_READING_CONTEXT")
                }
                AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> {
                    Log.d(TAG, "TYPE_GESTURE_DETECTION_END")
                }
                AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> {
                    Log.d(TAG, "TYPE_GESTURE_DETECTION_START")
                }
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    Log.d(TAG, "TYPE_NOTIFICATION_STATE_CHANGED")
                }
                AccessibilityEvent.TYPE_SPEECH_STATE_CHANGE -> {
                    Log.d(TAG, "TYPE_SPEECH_STATE_CHANGE")
                }
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> {
                    Log.d(TAG, "TYPE_TOUCH_EXPLORATION_GESTURE_END")
                }
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> {
                    Log.d(TAG, "TYPE_TOUCH_EXPLORATION_GESTURE_START")
                }
                AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> {
                    Log.d(TAG, "TYPE_TOUCH_INTERACTION_END")
                }
                AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> {
                    Log.d(TAG, "TYPE_TOUCH_INTERACTION_START")
                }

                AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> {
                    Log.d(TAG, "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED")
                }
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                    Log.d(TAG, "TYPE_VIEW_FOCUSED")
                    //Log.d(TAG, "${myEvent.className}")
                }
                AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> {
                    Log.d(TAG, "TYPE_VIEW_HOVER_ENTER")
                }
                AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> {
                    Log.d(TAG, "TYPE_VIEW_HOVER_EXIT")
                }
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    //Log.d(TAG, "TYPE_VIEW_SCROLLED")
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    Log.d(TAG, "TYPE_VIEW_TEXT_CHANGED")
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                    Log.d(TAG, "TYPE_VIEW_TEXT_SELECTION_CHANGED")
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> {
                    Log.d(TAG, "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY")
                }
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    //Log.d(TAG, "TYPE_WINDOW_CONTENT_CHANGED")
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    Log.d(TAG, "TYPE_WINDOW_STATE_CHANGED")
                    Log.d(TAG, "${myEvent.className}")
                }
                else -> {
                    Log.d(TAG, "fuck")
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
                Log.d(TAG, "kill event: ${event.text}|${event.packageName}")
                killPackageIfRunning(
                    this@MyAccessibilityService,
                    packageName.toString()
                )

                createMessage(
                    this@MyAccessibilityService,
                    "App Blocker",
                    "you can't access app settings while blocking"
                )
                /*Toast.makeText(
                    this@MyAccessibilityService,
                    "Sorry you can't access app settings for now",
                    Toast.LENGTH_LONG
                ).show()*/
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

            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            // If you only want this service to work with specific applications, set their
            // package names here. Otherwise, when the service is activated, it will listen
            // to events from all applications.
            packageNames = arrayOf(
                "com.android.settings",
                "com.android.launcher"
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