package com.android_a865.appblocker.feature_home.presentation

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.appblocker.feature_home.domain.AppsPackage
import com.android_a865.appblocker.feature_home.domain.PkgsRepository
import com.android_a865.appblocker.utils.isPermissionsGranted
import com.android_a865.appblocker.utils.requestBox
import com.android_a865.appblocker.utils.requestPermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PkgsRepository
) : ViewModel() {

    val pkgs = repository.getPkgs()
    val isActive = MutableLiveData(false)


    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()


    fun onFabClicked(context: Context) {
        val editText = EditText(context)
        AlertDialog.Builder(context)
            .setMessage("Enter Package Name")
            .setView(editText)
            .setPositiveButton("DONE") { d, _ ->
                addPackage(editText.text.toString())
                d.dismiss()
            }.setNegativeButton("CANCEL") { d, _ ->
                d.dismiss()
            }.show()
    }

    private fun addPackage(name: String) = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                HomeFragmentDirections.actionHomeFragmentToChooseAppsFragment(
                    pkgName = name
                )
            )
        )
    }

    fun onItemClicked(item: AppsPackage?) = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                HomeFragmentDirections.actionHomeFragmentToChooseAppsFragment(
                    appPkg = item
                )
            )
        )
    }

    fun onDeleteItemClicked(pkg: AppsPackage) = viewModelScope.launch {
        repository.deletePkg(pkg)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onBlockPackageClicked(context: Context, pkg: AppsPackage) {
        if (isPermissionsGranted(context)) {
            block(pkg)
        } else {
            requestBox(
                context,
                "App Blocker",
                "We need some permissions to work properly"
            ) {
                requestPermissions(context)
            }
        }


    }

    private fun block(pkg: AppsPackage) = viewModelScope.launch {
        pkg.isActive = true

        eventsChannel.send(
            WindowEvents.Navigate(
                HomeFragmentDirections.actionHomeFragmentToChooseAppsFragment(
                    appPkg = pkg
                )
            )
        )
    }

    fun onPkgsSubmitted(pkgs: List<AppsPackage>) = viewModelScope.launch {
        val pkg = pkgs.filter {
            it.isActive
        }

        if (pkg.isNotEmpty() && pkg.size == 1) {
            // go to pkg view
            eventsChannel.send(
                WindowEvents.Navigate(
                    HomeFragmentDirections.actionHomeFragmentToChooseAppsFragment(
                        appPkg = pkg[0]
                    )
                )
            )
        } else {
            // submit to adapter
            eventsChannel.send(
                WindowEvents.AdapterSubmit(
                    pkgs
                )
            )
        }

    }


    sealed class WindowEvents {
        data class Navigate(val direction: NavDirections) : WindowEvents()
        data class AdapterSubmit(val pkgs: List<AppsPackage>) : WindowEvents()

    }
}