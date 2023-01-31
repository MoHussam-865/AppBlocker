package com.android_a865.appblocker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.common.AppFetcher
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.databinding.ActivityMainBinding
import com.android_a865.appblocker.models.App
import com.android_a865.appblocker.services.BackgroundManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onItemClicked(app: App, isChecked: Boolean) {

        GlobalScope.launch {
            installedApps.forEachIndexed { index, application ->
                if (application.packageName == app.packageName) {
                    installedApps[index] = application.copy(selected = isChecked)
                }
            }

            PreferencesManager.setLockedApps(
                this@MainActivity,
                installedApps.filter { it.selected }
            )
            refresh()
        }

    }

    private fun refresh() {
        val sortedArray = ArrayList<App>()

        sortedArray.addAll(installedApps.filter { it.selected })
        sortedArray.addAll(installedApps.filter { !it.selected }.sortedBy { it.name })

        installedApps = sortedArray

        blockedAppsAdapter.submitList(installedApps as List<App>)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun block(time: Int) {
        Toast.makeText(this, "Blocking Started", Toast.LENGTH_SHORT).show()

        GlobalScope.launch {
            val endTime = System.currentTimeMillis() + time * 60000

            val allApps = AppFetcher
                .getAllInstalledApplications(this@MainActivity)
            val blockedApps = installedApps.filter { it.selected }
            val blockedAppsPackage = blockedApps.map { it.packageName }
            val allowedApps = allApps.filter {
                it.packageName !in blockedAppsPackage
            }

            /*PreferencesManager.setAllowedApps(this, allowedApps)
            PreferencesManager.setLockedApps(this, blockedApps)
            PreferencesManager.setEndTime(this, endTime)
            PreferencesManager.setLastTime(this, time)*/
            PreferencesManager.setupLockSettings(
                this@MainActivity,
                endTime,
                blockedApps,
                allowedApps,
                time
            )
            BackgroundManager.instance?.init(this@MainActivity)?.startService()


        }


        //val intent = Intent(this@MainActivity, )
    }

}