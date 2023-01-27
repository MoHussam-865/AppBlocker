package com.android_a865.appblocker

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val blockedAppsAdapter = BlockedAppsAdapter()

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
        blockedAppsAdapter.submitList(AppFetcher.getInstalledApplications(this))
    }

}