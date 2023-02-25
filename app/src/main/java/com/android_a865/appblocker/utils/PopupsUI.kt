package com.android_a865.appblocker.utils

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android_a865.appblocker.R
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.common.services.BackgroundManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun createMessage(
    context: Context,
    title: String,
    msg: String
) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }.show()
}

// does not work properly
fun accessibilityRequestMessage(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.app_name))
        .setMessage(context.getString(R.string.accessibility_message))
        .setPositiveButton("OK") { dialog, _ ->

            //getAccessibilityPermission(context)

            dialog.dismiss()
        }.show()
}

// does not work properly
@OptIn(DelicateCoroutinesApi::class)
suspend fun loadingProgress(context: Context, func: suspend () -> Unit) {
    val dialog = ProgressDialog(context)
    dialog.setTitle("Loading....")
    dialog.setCancelable(false)
    dialog.show()
    delay(1000)
    func()
    dialog.dismiss()

}

fun requestBox(
    context: Context,
    title: String,
    msg: String,
    func: () -> Unit
) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("OK") { _, _ ->
            func()
        }.show()
}

