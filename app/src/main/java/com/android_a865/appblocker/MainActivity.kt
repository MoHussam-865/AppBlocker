package com.android_a865.appblocker

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.admin.MyDeviceAdminReceiver
import com.android_a865.appblocker.common.AppFetcher
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.databinding.ActivityMainBinding
import com.android_a865.appblocker.models.App
import com.android_a865.appblocker.services.BackgroundManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), BlockedAppsAdapter.OnItemEventListener {

    private var installedApps = ArrayList<App>()
    private lateinit var blockedAppsAdapter: BlockedAppsAdapter


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blockedAppsAdapter = BlockedAppsAdapter(this, this)

        requestPermissions()

        getApplications()

        binding.apply {

            blockTime.editText?.setText(
                PreferencesManager
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
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity,"Enter Time", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun getApplications() {
        installedApps = AppFetcher.getApps(this)
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

        blockedAppsAdapter.submitList(installedApps)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @OptIn(DelicateCoroutinesApi::class)
    private fun block(time: Int) {

        if (installedApps.any { it.selected }) {
            Toast.makeText(this, "Blocking Started", Toast.LENGTH_SHORT).show()
            refresh()

            GlobalScope.launch {
                PreferencesManager.setupLockSettings(
                    this@MainActivity,
                    installedApps,
                    time
                )
                BackgroundManager.instance?.startService(this@MainActivity)
            }
        }

        else {
            Toast.makeText(this,"No apps selected",Toast.LENGTH_SHORT).show()
        }

        //val intent = Intent(this@MainActivity, )
    }


    private fun requestPermissions() {
        val mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        if (!mDPM.isAdminActive(adminName)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName)
            startActivityForResult(intent, 0)
        }
    }
}