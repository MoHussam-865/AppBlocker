package com.android_a865.appblocker.feature_home.presentation

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.appblocker.feature_home.domain.AppsPackage
import com.android_a865.appblocker.feature_home.domain.PkgsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PkgsRepository
): ViewModel() {

    val pkgs = repository.getPkgs()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()


    fun onFabClicked(context: Context) {
        val editText = EditText(context)
        AlertDialog.Builder(context)
            .setMessage("Enter Package Name")
            .setView(editText)
            .setPositiveButton("DONE") { d ,_ ->
                addPackage(editText.text.toString())
                d.dismiss()
            }.setNegativeButton("CANCEL") { d ,_ ->
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

    fun onBlockPackageClicked(pkg: AppsPackage) {
        // TODO
    }


    sealed class WindowEvents {
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}