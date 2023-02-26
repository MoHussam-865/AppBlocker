package com.android_a865.appblocker.utils

import android.content.Context
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.feature_choose_apps.domain.App
import com.android_a865.appblocker.feature_home.data.PkgEntity
import com.android_a865.appblocker.feature_home.domain.AppsPackage
import java.util.ArrayList

val <T> T.exhaustive: T get() = this


fun AppsPackage.toEntity() = PkgEntity(
        name = name,
        time = time,
        isActive = isActive,
        apps = apps.joinToString("/")
    )


fun PkgEntity.toDomain() = AppsPackage(
        name = name,
        time = time,
        isActive = isActive,
        apps = apps.split("/").filter { it != "" }
    )



fun ArrayList<App>.arrange(): ArrayList<App> {
    val sortedArray = ArrayList<App>()
    sortedArray.addAll(filter { it.active }.sortedBy { it.name })
    sortedArray.addAll(filter { it.selected && !it.active }.sortedBy { it.name })
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
    context: Context,
    pkg: AppsPackage
): ArrayList<App> {

    val selectedApps = pkg.apps

    if (!PreferencesManager.isActive(context)) {
        PreferencesManager.clearActiveLockedApps(context)
    }
    val activeLockedApps = PreferencesManager.getActiveLockedApps(context)

    forEach {
        it.selected = selectedApps.contains(it.packageName)
        it.active = activeLockedApps.contains(it.packageName)
    }
    return this

    /*val endTime = PreferencesManager.getEndTime(context)
    val lockedApps = PreferencesManager.getLockedApps(context)
    if (endTime < System.currentTimeMillis()) {
        PreferencesManager.clearActiveLockedApps(context)
    }
    val activeLockedApps = PreferencesManager.getActiveLockedApps(context)

    forEach {
        it.selected = lockedApps.contains(it.packageName)
        it.isActive = activeLockedApps.contains(it.packageName)
    }
    return this*/
}



fun List<String>.containsIgnoreCase(str: String): Boolean {
    forEach {
        if (it.equals(str, ignoreCase = true)){
            return true
        }
    }
    return false
}
