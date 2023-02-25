package com.android_a865.appblocker.feature_Splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android_a865.appblocker.R
import com.android_a865.appblocker.common.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Thread.sleep(1500)
        val dir = if (PreferencesManager.isActive(requireContext())) {
            SplashFragmentDirections.actionSplashFragmentToChooseAppsFragment()
        } else {
            SplashFragmentDirections.actionSplashFragmentToHomeFragment()
        }
        findNavController().navigate(dir)
    }

}