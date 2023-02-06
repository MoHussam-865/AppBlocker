package com.android_a865.appblocker

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process.myUid
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.admin.MyDeviceAdminReceiver
import com.android_a865.appblocker.common.AppFetcher
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.databinding.ActivityMainBinding
import com.android_a865.appblocker.models.App
import com.android_a865.appblocker.services.BackgroundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), BlockedAppsAdapter.OnItemEventListener {

    private val installedApps = MutableLiveData<ArrayList<App>>(ArrayList())
    private val isActive = MutableLiveData(false)
    private var apps
        get() = installedApps.value!!
        set(value) { installedApps.value = value }

    private val blockedAppsAdapter = BlockedAppsAdapter(this)


    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        installedApps.observe(this) { list ->
            blockedAppsAdapter.submitList(list)

            PreferencesManager.setLockedApps(
                this@MainActivity,
                list.filter {app -> app.selected }
            )
        }

        isActive.observe(this) {
            blockedAppsAdapter.isActive = it
            blockedAppsAdapter.notifyDataSetChanged()
        }

        isActive.value = PreferencesManager.isActive(this)
    }

    private fun getApplications() {
        val allApps = AppFetcher.getApps(this) as ArrayList<App>
        val packages = PreferencesManager.getLockedApps(this)

        allApps.forEach {
            if (packages.contains(it.packageName)) {
                it.selected = true
            }
        }
        apps = rearrange(allApps)
        //refresh()
    }

    override fun onItemClicked(app: App, isChecked: Boolean) {
        lifecycleScope.launch {
            apps.forEachIndexed { index, application ->
                if (application.packageName == app.packageName) {
                    apps[index] = application.copy(selected = isChecked)
                }
            }
            apps = rearrange(apps)
        }
    }

    private fun rearrange(list: List<App>): ArrayList<App> {
        val sortedArray = ArrayList<App>()
        sortedArray.addAll(list.filter { it.selected })
        sortedArray.addAll(list.filter { !it.selected }.sortedBy { it.name })
        return sortedArray
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun block(time: Int) {

        // time is in minute, the day has (24*60) minute
        if (time > (24 * 60)) {
            Toast.makeText(
                this,
                "Can't block for more than 1 day",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (apps.any { it.selected }) {
            // disable checkBoxes
            isActive.value = true
            Toast.makeText(this, "Blocking Started", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                // saves the data to start blocking
                PreferencesManager.setupLockSettings(
                    this@MainActivity,
                    apps,
                    time
                )
                // enable the checkBoxes when service ends
                observeList()
                // start the blocking service
                BackgroundManager.instance?.startService(this@MainActivity)
            }
        }
        else {
            Toast.makeText(this,"No apps selected",Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {
        // Usage State permission needed to know the current running app
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            myUid(),
            packageName
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        /*if (!isAccessibilitySettingsOn(this)) {
            startActivityForResult(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                156
            )
        }*/

        // display over other apps
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 0)
        }

        // admin permission
        val mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        if (!mDPM.isAdminActive(adminName)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName)
            startActivityForResult(intent, 0)
        }

    }

    private fun observeList() {
        lifecycleScope.launch {
            val endTime = PreferencesManager.getEndTime(this@MainActivity)
            while (endTime > System.currentTimeMillis()) {
                delay(3000)
            }
            isActive.value = false
        }
    }

}