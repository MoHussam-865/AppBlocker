package com.android_a865.appblocker

import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.databinding.ActivityMainBinding
import com.android_a865.appblocker.models.App
import com.android_a865.appblocker.common.AppFetcher
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.services.BackgroundManager


class MainActivity : AppCompatActivity(), BlockedAppsAdapter.OnItemEventListener {

    private val blockedAppsAdapter = BlockedAppsAdapter(this)
    private var installedApps = ArrayList<App>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getApplications()

        binding.apply {

            blockTime.editText?.setText(PreferencesManager
                .getLastTime(this@MainActivity)
                .toString()
            )

            blockedAppsList.apply {
                adapter = blockedAppsAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }


            start.setOnClickListener {
                try {
                    block(
                        blockTime.editText?.text.toString().toInt()
                    )
                } catch (e: Exception) {  }

            }


        }
    }

    private fun getApplications() {
        installedApps = AppFetcher.getInstalledApplications(this)
                as ArrayList<App>
        val packages = PreferencesManager.getLockedApps(this)

        installedApps.forEach {
            if (packages.contains(it.packageName)) {
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

    private fun block(time: Int) {
        val endTime = System.currentTimeMillis() + time * 60000
        PreferencesManager.setEndTime(this@MainActivity, endTime)
        PreferencesManager.setLastTime(this@MainActivity, time)
        PreferencesManager.setAllowedApps(
            this@MainActivity,
            installedApps.filter { !it.selected }
        )



        // TODO Allow Allowed apps and block all the others
        //val intent = Intent(this@MainActivity, )
        BackgroundManager.instance?.init(this@MainActivity)?.startService()
        Toast.makeText(
            this@MainActivity,
            "Blocking Started",
            Toast.LENGTH_SHORT
        ).show()
    }

}