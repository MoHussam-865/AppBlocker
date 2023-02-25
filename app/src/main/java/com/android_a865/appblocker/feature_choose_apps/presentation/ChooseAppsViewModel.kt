package com.android_a865.appblocker.feature_choose_apps.presentation

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.appblocker.common.AppFetcher
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.common.services.BackgroundManager
import com.android_a865.appblocker.feature_choose_apps.domain.App
import com.android_a865.appblocker.feature_home.domain.AppsPackage
import com.android_a865.appblocker.feature_home.domain.PkgsRepository
import com.android_a865.appblocker.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseAppsViewModel @Inject constructor(
    state: SavedStateHandle,
    private val repository: PkgsRepository
) : ViewModel() {

    private val pkg = state.get<AppsPackage>("app_pkg")
    private val pkgName = state.get<String>("pkg_name")

    val installedApps = MutableLiveData<ArrayList<App>>()
    private var apps
        get() = installedApps.value!!
        set(value) {
            installedApps.value = value
        }

    val isActive = MutableLiveData(pkg?.isActive ?: false)

    var lastTime: Int = pkg?.time ?: 0
    // so we don't need to clear the zero every time
    val editTextValue: String = if (lastTime>0) lastTime.toString() else ""

    private val itemsWindowEventsChannel = Channel<MyWindowEvents>()
    val itemsWindowEvents = itemsWindowEventsChannel.receiveAsFlow()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun initiate(context: Context) = viewModelScope.launch  {
        val myApps = AppFetcher.getApps(context)
        apps = if (pkg != null) {
            myApps.getSelected(context, pkg)
                .arrange()
        } else {
            myApps.arrange()
        }

        if (isActive.value == true) {
            loadingProgress(context) {

                // saves the data to start blocking
                PreferencesManager.setupLockSettings(
                    context,
                    apps,
                    pkg?.time!!
                )
                // start the blocking service
                BackgroundManager.startService(context)
                itemsWindowEventsChannel.send(
                    MyWindowEvents.NotifyAdapter
                )
                Toast.makeText(
                    context,
                    "Blocking started",
                    Toast.LENGTH_LONG
                ).show()
            }
        }



        /*isActive.value = PreferencesManager.isActive(context)

        lastTime = PreferencesManager
            .getLastTime(context)
            .toString()

        if (isActive.value!! && !isPermissionsGranted(context)) {
            requestBox(
                context,
                "App Blocker",
                "We need some permissions to work properly"
            ) {
                requestPermissions(context)
            }
        }
        */

    }


    fun onActiveStateChanges(context: Context) = viewModelScope.launch {
        // TODO
        // apps = apps.getSelected(context).arrange()

        itemsWindowEventsChannel.send(
            MyWindowEvents.NotifyAdapter
        )
    }

    fun onAppSelected(app: App, checked: Boolean) {
        apps = apps.selectApp(app, checked)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onFabClicked(context: Context, myTime: String) = viewModelScope.launch {
        // we just need to verify (time & selected apps)
        try {
            lastTime = myTime.toInt()

            // time is in minute, the day has (24*60) minute
            if (lastTime > (24 * 60) || lastTime == 0) {
                createMessage(
                    context,
                    "Time Error",
                    "can't block for less than 1 minute & more than 24 hours"
                )
            }
            else if (apps.any { it.selected }) {
                // then save the package
                repository.insertPkg(getPkgToSave())
                // go back
                itemsWindowEventsChannel.send(
                    MyWindowEvents.GoBack
                )
            }
            else {
                Toast.makeText(context, "No apps selected", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Enter Time",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun getPkgToSave(): AppsPackage {
        return AppsPackage(
            name = pkg?.name ?: pkgName ?: "",
            time = lastTime,
            apps = apps.filter { it.selected }.map { it.packageName }
        )
    }


    /*
    @RequiresApi(Build.VERSION_CODES.S)
    private suspend fun block(context: Context, time: Int) {

        // time is in minute, the day has (24*60) minute
        if (time > (24 * 60)) {
            createMessage(
                context,
                "Sorry",
                "can't block for more than 24 hours"
            )
            return
        }

        if (apps.any { it.selected }) {

            loadingProgress(context) {

                // disable checkboxes
                isActive.value = true

                // saves the data to start blocking
                PreferencesManager.setupLockSettings(
                    context,
                    apps,
                    time
                )
                // start the blocking service
                BackgroundManager.startService(context)
                Toast.makeText(
                    context,
                    "Blocking started",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {
            Toast.makeText(context, "No apps selected", Toast.LENGTH_LONG).show()
        }
    }
    */

/*
    private fun observeList(context: Context) = viewModelScope.launch {

        var lastValue = false
        var dataValue: Boolean
        while (true) {
            dataValue = PreferencesManager.isActive(context)
            if (lastValue != dataValue) {
                isActive.value = dataValue
                lastValue = dataValue
            }
            delay(3000)
        }

    }
*/


    sealed class MyWindowEvents {
        object NotifyAdapter : MyWindowEvents()
        object GoBack : MyWindowEvents()
    }
}