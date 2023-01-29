package com.android_a865.appblocker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), BlockedAppsAdapter.OnItemEventListener {

    private val blockedAppsAdapter = BlockedAppsAdapter(this)
    private var installedApps = ArrayList<App>()

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
                val time = blockTime.editText?.text.toString()

            }


        }
    }

    private fun getApplications() {
        installedApps = AppFetcher.getInstalledApplications(this)
                as ArrayList<App>
        val packages = PreferencesManager.readData(this)

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
        
        PreferencesManager.writeData(this, installedApps.filter { it.selected })
        refresh()
    }

    private fun refresh() {
        val sortedArray = ArrayList<App>()

        installedApps.forEach { if (it.selected) sortedArray.add(it) }

        installedApps.forEach { if (!it.selected) sortedArray.add(it) }

        installedApps = sortedArray

        blockedAppsAdapter.submitList(installedApps as List<App>)
    }
}