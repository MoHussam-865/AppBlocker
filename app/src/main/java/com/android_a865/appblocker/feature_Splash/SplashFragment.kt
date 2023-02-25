package com.android_a865.appblocker.feature_Splash

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
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
    private val tag = "app_dep"

    @Inject
    lateinit var repository: PkgsRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Thread.sleep(1000)
        if (PreferencesManager.isActive(requireContext())) {
            lifecycleScope.launch {
                val pkg = repository.getActivePkg()
                Log.d(tag, "blocking Active ${pkg?.name}")
                findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToChooseAppsFragment(
                        appPkg = pkg,
                        alreadyActive = true
                    )
                )
            }
        } else {
            lifecycleScope.launch {
                Log.d(tag, "blocking Active")
                repository.clearActiveBlock()

                findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                )
            }
        }

    }

}