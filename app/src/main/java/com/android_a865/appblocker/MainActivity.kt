package com.android_a865.appblocker

import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), BlockedAppsAdapter.OnItemEventListener {

    private val blockedAppsAdapter = BlockedAppsAdapter(this)
    private var installedApps = emptyList<App>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getApplications()

        binding.apply {

            blockedAppsList.apply {
                adapter = blockedAppsAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }

            start.setOnClickListener {
                val time = blockTime.editText?.text.toString().toInt()

            }


        }
    }

    private fun getApplications() {
        installedApps = AppFetcher.getInstalledApplications(this)
        val packages = PreferencesManager.readData(this)
        installedApps.forEach {
            if (it.packageName in packages) {
                it.selected = true
            }
        }

    }

    override fun onItemClicked(app: App) {
        refresh()
    }

    private fun refresh() {
        installedApps.sortedByDescending {
            it.selected
        }

        blockedAppsAdapter.submitList(installedApps)
    }

}