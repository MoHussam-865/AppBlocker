package com.android_a865.appblocker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.databinding.ActivityMainBinding
import com.android_a865.appblocker.models.App
import com.android_a865.appblocker.services.AppFetcher
import com.android_a865.appblocker.services.PreferencesManager


class MainActivity : AppCompatActivity(), BlockedAppsAdapter.OnItemEventListener {

    private val blockedAppsAdapter = BlockedAppsAdapter(this)
    private var installedApps = ArrayList<App>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getApplications()

        binding.apply {

            start.isVisible = PreferencesManager
                .getEndTime(this@MainActivity) <= System
                .currentTimeMillis()

            blockedAppsList.apply {
                adapter = blockedAppsAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }

            start.setOnClickListener {
                try {
                    val time = blockTime.editText?.text.toString().toInt()
                    val endTime = System.currentTimeMillis() + time * 60000
                    PreferencesManager.setEndTime(this@MainActivity, endTime)
                    PreferencesManager.setAllowedApps(
                        this@MainActivity,
                        installedApps.filter { !it.selected }
                    )

                    // TODO Allow Allowed apps and block all the others
                    Toast.makeText(
                        this@MainActivity,
                        "Blocking Started",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {  }

            }


        }
    }

    private fun getApplications() {
        installedApps = AppFetcher.getInstalledApplications(this)
                as ArrayList<App>
        val packages = PreferencesManager.getLockedApps(this)

        installedApps.forEach {
            if (it.packageName in packages) {
                it.selected = true
            }
        }
        refresh()
    }

    override fun onItemClicked(app: App, isChecked: Boolean) {
        installedApps.forEachIndexed { index, application ->
            if (application.packageName == app.packageName) {
                installedApps[index] = application.copy(selected = isChecked)
            }
        }

        PreferencesManager.setLockedApps(this, installedApps.filter { it.selected })
        refresh()
    }

    private fun refresh() {
        val sortedArray = ArrayList<App>()

        sortedArray.addAll(installedApps.filter { it.selected })
        sortedArray.addAll(installedApps.filter { !it.selected }.sortedBy { it.name })

        installedApps = sortedArray

        blockedAppsAdapter.submitList(installedApps as List<App>)
    }
}