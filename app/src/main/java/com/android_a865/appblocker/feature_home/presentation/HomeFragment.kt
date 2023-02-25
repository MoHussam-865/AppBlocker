package com.android_a865.appblocker.feature_home.presentation

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.R
import com.android_a865.appblocker.common.adapters.HomeListAdapter
import com.android_a865.appblocker.databinding.FragmentHomeBinding
import com.android_a865.appblocker.feature_home.domain.AppsPackage
import com.android_a865.appblocker.utils.exhaustive
import com.android_a865.appblocker.utils.loadingProgress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
    HomeListAdapter.OnEventListener {

    private val pkgAdapter = HomeListAdapter(this)
    private val viewModel by viewModels<HomeViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)

        binding.apply {

            blockedAppsList.apply {
                adapter = pkgAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }

            fab.setOnClickListener {
                viewModel.onFabClicked(requireContext())
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is HomeViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                    }
                }.exhaustive

            }
        }

        viewModel.pkgs.asLiveData().observe(viewLifecycleOwner) {
            pkgAdapter.submitList(it)
        }


        viewModel.initiate(requireContext())
    }

    override fun onDeleteClicked(pkg: AppsPackage) {
        viewModel.onDeleteItemClicked(pkg)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartClicked(pkg: AppsPackage) {
        viewModel.onBlockPackageClicked(requireContext(), pkg)
    }

    override fun onItemClicked(item: AppsPackage?) {
        viewModel.onItemClicked(item)
    }
}