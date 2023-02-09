package com.android_a865.appblocker.utils

import android.content.Context
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.models.App
import java.util.ArrayList

fun ArrayList<App>.arrange(): ArrayList<App> {
    val sortedArray = ArrayList<App>()
    sortedArray.addAll(filter { it.selected })
    sortedArray.addAll(filter { !it.selected }.sortedBy { it.name })
    return sortedArray
}

fun ArrayList<App>.selectApp(
    app: App,
    isChecked: Boolean
): ArrayList<App> {
    forEachIndexed { index, application ->
        if (application.packageName == app.packageName) {
            set(index, application.copy(selected = isChecked))
        }
    }
    return arrange()
}


fun ArrayList<App>.getSelected(
    context: Context
): ArrayList<App> {
    val packages = PreferencesManager.getLockedApps(context)
    forEach {
        if (packages.contains(it.packageName)) {
            it.selected = true
        }
    }
    return this
}

fun List<String>.containsIgnoreCase(str: String): Boolean {
    forEach {
        if (it.equals(str, ignoreCase = true)){
            return true
        }
    }
    return false
}
