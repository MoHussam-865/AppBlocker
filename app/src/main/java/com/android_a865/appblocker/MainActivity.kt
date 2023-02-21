package com.android_a865.appblocker

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.common.AppFetcher
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.databinding.ActivityMainBinding
import com.android_a865.appblocker.models.App
import com.android_a865.appblocker.services.BackgroundManager
import com.android_a865.appblocker.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), BlockedAppsAdapter.OnItemEventListener {

    private val installedApps = MutableLiveData<ArrayList<App>>(ArrayList())
    private var apps
        get() = installedApps.value!!
        set(value) {
            installedApps.value = value
        }

    private val isActive = MutableLiveData(false)
    private val blockedAppsAdapter = BlockedAppsAdapter(this)


    @RequiresApi(33)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    if (isPermissionsGranted(this@MainActivity)) {
                        block(
                            blockTime.editText?.text.toString().toInt()
                        )
                    } else {
                        requestBox(
                            this@MainActivity,
                            "App Blocker",
                            "We need some permissions to work properly"
                        ) {
                            requestPermissions(this@MainActivity)
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Enter Time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        installedApps.observe(this) { list ->
            blockedAppsAdapter.submitList(list)

            PreferencesManager.setLockedApps(
                this@MainActivity,
                list.filter { app -> app.selected }
            )
        }


        isActive.value = PreferencesManager.isActive(this)

        if (isActive.value!! && !isPermissionsGranted(this@MainActivity)) {
            requestBox(
                this@MainActivity,
                "App Blocker",
                "We need some permissions to work properly"
            ) {
                requestPermissions(this@MainActivity)
            }
        }

        isActive.observe(this) {
            apps = apps.getSelected(this).arrange()
            blockedAppsAdapter.notifyDataSetChanged()
        }

        observeList()
    }

    private fun getApplications() {
        apps = AppFetcher.getApps(this).arrange()
    }

    override fun onItemClicked(app: App, isChecked: Boolean) {
        apps = apps.selectApp(app, isChecked)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun block(time: Int) {

        // time is in minute, the day has (24*60) minute
        if (time > (24 * 60)) {
            createMessage(
                this,
                "Sorry",
                "can't block for more than 24 hours"
            )
            return
        }

        if (apps.any { it.selected }) {

            lifecycleScope.launch {
                loadingProgress(this@MainActivity) {

                    // disable checkboxes
                    isActive.value = true

                    // saves the data to start blocking
                    PreferencesManager.setupLockSettings(
                        this@MainActivity,
                        apps,
                        time
                    )
                    // start the blocking service
                    BackgroundManager.startService(this@MainActivity)
                    Toast.makeText(
                        this@MainActivity,
                        "Blocking started",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "No apps selected", Toast.LENGTH_LONG).show()
        }
    }


    private fun observeList() {
        // enable the checkBoxes when service ends
        lifecycleScope.launch {

            var lastValue = false
            var dataValue: Boolean
            while (true) {
                dataValue = PreferencesManager.isActive(this@MainActivity)
                if (lastValue != dataValue) {
                    isActive.value = dataValue
                    lastValue = dataValue
                }
                delay(3000)
            }
        }
    }
}