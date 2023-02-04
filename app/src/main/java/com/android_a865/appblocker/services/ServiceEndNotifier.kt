package com.android_a865.appblocker.services

import android.app.IntentService
import android.content.Intent
import android.util.Log

class ServiceEndNotifier: IntentService("end_service") {


    @Deprecated("Deprecated in Java")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val myIntent = Intent()
        myIntent.action = END_MSG
        sendBroadcast(myIntent)
        stopService(Intent(this, this.javaClass))
        Log.d("app_running", "activity stopping")
        return super.onStartCommand(myIntent, flags, startId)
    }


    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) { }

    companion object {
        const val END_MSG = "service_end"
    }

}