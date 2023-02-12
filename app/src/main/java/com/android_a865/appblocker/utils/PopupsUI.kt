package com.android_a865.appblocker.utils

import android.app.AlertDialog
import android.content.Context
import com.android_a865.appblocker.R


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
fun loadingWindow(context: Context): AlertDialog.Builder {
    val dialog =  AlertDialog.Builder(context)
        .setCancelable(false)
        .setTitle("Loading....")
        .setMessage("please wait")

    return dialog
}

fun requestBox(
    context: Context,
    title: String,
    msg: String,
    func: ()->Unit
) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("OK") { _, _ ->
            func()
        }.show()
}

