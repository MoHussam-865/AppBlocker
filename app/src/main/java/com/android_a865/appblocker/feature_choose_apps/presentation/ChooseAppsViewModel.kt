package com.android_a865.appblocker.feature_choose_apps.presentation

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.appblocker.common.AppFetcher
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.common.services.BackgroundManager
import com.android_a865.appblocker.feature_choose_apps.domain.App
import com.android_a865.appblocker.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseAppsViewModel @Inject constructor() : ViewModel() {

    val installedApps = MutableLiveData<ArrayList<App>>(ArrayList())
    private var apps
        get() = installedApps.value!!
        set(value) {
            installedApps.value = value
        }

    val isActive = MutableLiveData(false)

    var lastTime: String = "0"

    private val itemsWindowEventsChannel = Channel<MyWindowEvents>()
    val itemsWindowEvents = itemsWindowEventsChannel.receiveAsFlow()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun initiate(context: Context) {
        apps = AppFetcher.getApps(context).arrange()

        isActive.value = PreferencesManager.isActive(context)

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

        // TODO observe list
        observeList(context)
    }

    fun onSelectedAppsChange(context: Context) {
        PreferencesManager.setLockedApps(
            context,
            apps.filter { app -> app.selected }
        )
    }

    fun onActiveStateChanges(context: Context) = viewModelScope.launch {
        apps = apps.getSelected(context).arrange()

        itemsWindowEventsChannel.send(
            MyWindowEvents.NotifyAdapter
        )
    }

    fun onAppSelected(app: App, checked: Boolean) {
        apps = apps.selectApp(app, checked)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onStartBlockingPressed(context: Context, time: String) = viewModelScope.launch {
        try {
            if (isPermissionsGranted(context)) {
                block(
                    context,
                    time.toInt()
                )
            } else {
                requestBox(
                    context,
                    "App Blocker",
                    "We need some permissions to work properly"
                ) {
                    requestPermissions(context)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Enter Time",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


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


    sealed class MyWindowEvents {
        object NotifyAdapter : MyWindowEvents()
        /*object Loading : MyWindowEvents()
        data class ToastMessage(val msg: String) : MyWindowEvents()
        data class MessageBox(val msg: String) : MyWindowEvents()*/
    }
}