package com.android_a865.appblocker.feature_choose_apps.presentation

import android.content.Context
import android.os.Build
import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "app_dep"
@HiltViewModel
class ChooseAppsViewModel @Inject constructor(
    state: SavedStateHandle,
    private val repository: PkgsRepository
) : ViewModel() {

    private var pkg = state.get<AppsPackage>("app_pkg")
    private var pkgName = pkg?.name ?: state.get<String>("pkg_name") ?: ""
    private var alreadyActive = state.get<Boolean>("already_active") ?: false

    val active = MutableStateFlow(pkg?.isActive ?: false)
    var lastTime: Int = pkg?.time ?: 1

    // so we don't need to clear the zero every time


    val installedApps = MutableLiveData<ArrayList<App>>()
    private var apps
        get() = installedApps.value!!
        set(value) {
            installedApps.value = value
        }


    private val itemsWindowEventsChannel = Channel<MyWindowEvents>()
    val itemsWindowEvents = itemsWindowEventsChannel.receiveAsFlow()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun initiate(context: Context) = viewModelScope.launch {
        apps = getApps(context)

        Log.d(TAG, "${active.value}")
        Log.d(TAG, "${pkg?.isActive}")
        Log.d(TAG, "${pkg?.name}")

        if (active.value && !alreadyActive) blockPackage(context)
    }

    private fun getApps(context: Context): ArrayList<App> {
        val myApps = AppFetcher.getApps(context)
        pkg?.let {
            return myApps.getSelected(context, it)
                .arrange()
        }
        return myApps.arrange()
    }

    fun onAppSelected(app: App, checked: Boolean) {
        apps = apps.selectApp(app, checked)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onFabClicked(context: Context, myTime: String) = viewModelScope.launch {
        // we just need to verify (time & selected apps)
        try {
            lastTime = myTime.toInt()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Time set to default",
                Toast.LENGTH_SHORT
            ).show()
        }

            // time is in minute, the day has (24*60) minute
            if (lastTime > (24 * 60) || lastTime == 0) {
                createMessage(
                    context,
                    "Time Error",
                    "can't block for less than 1 minute & more than 24 hours"
                )
            } else if (apps.any { it.selected }) {

                if (active.value) {
                    // add new selected apps to the blocking
                    /**
                        no needed to insert the pkg because
                        blockPackage fun already dose that
                     */
                    blockPackage(context)
                } else {
                    // then save the package
                    /** (needed here) */
                    repository.insertPkg(getPkgToSave())
                    // go back
                    itemsWindowEventsChannel.send(
                        MyWindowEvents.GoBack
                    )
                }

            } else {
                Toast.makeText(context, "No apps selected", Toast.LENGTH_LONG).show()
            }

    }


    private fun getPkgToSave(): AppsPackage {
        return AppsPackage(
            name = pkgName,
            time = lastTime,
            apps = apps.filter { it.selected }.map { it.packageName },
            isActive = active.value
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private suspend fun blockPackage(context: Context) {
        loadingProgress(context) {

            // saves the data to start blocking
            PreferencesManager.setupLockSettings(
                context,
                apps,
                pkg?.time!!
            )
            repository.insertPkg(getPkgToSave())

            // start the blocking service
            BackgroundManager.startService(context)

            // disable checkboxes
            apps = apps.getSelected(context, getPkgToSave()).arrange()
            itemsWindowEventsChannel.send(
                MyWindowEvents.NotifyAdapter(apps)
            )

            Toast.makeText(
                context,
                "Blocking started",
                Toast.LENGTH_LONG
            ).show()
        }

    }


    sealed class MyWindowEvents {
        data class NotifyAdapter(val apps: List<App>) : MyWindowEvents()
        object GoBack : MyWindowEvents()
    }
}