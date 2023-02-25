package com.android_a865.appblocker.feature_Splash

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.appblocker.R
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.feature_home.domain.PkgsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val TAG = "app_dep"
    @Inject
    lateinit var repository: PkgsRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Thread.sleep(1000)
        if (PreferencesManager.isActive(requireContext())) {
            Log.d(TAG, "blocking Active")
            lifecycleScope.launch {
                findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToChooseAppsFragment(
                        appPkg = repository.getActivePkg(),
                        alreadyActive = true
                    )
                )
            }
        } else {
            lifecycleScope.launch {
                Log.d(TAG, "blocking Active")
                repository.clearActiveBlock()

                findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                )
            }
        }

    }

}