package com.android_a865.appblocker.feature_choose_apps.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.appblocker.R
import com.android_a865.appblocker.common.adapters.BlockedAppsAdapter
import com.android_a865.appblocker.databinding.FragmentChooseAppsBinding
import com.android_a865.appblocker.feature_choose_apps.domain.App
import com.android_a865.appblocker.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseAppsFragment : Fragment(R.layout.fragment_choose_apps),
    BlockedAppsAdapter.OnItemEventListener {

    private val blockedAppsAdapter = BlockedAppsAdapter(this)
    private val viewModel by viewModels<ChooseAppsViewModel>()

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChooseAppsBinding.bind(view)

        viewModel.initiate(requireContext())

        binding.apply {

            viewModel.editTextValue.asLiveData().observe(viewLifecycleOwner) {
                blockTime.editText?.setText(it)
            }

            blockedAppsList.apply {
                adapter = blockedAppsAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }

            start.setOnClickListener {
                viewModel.onFabClicked(
                    requireContext(),
                    blockTime.editText?.text.toString()
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.itemsWindowEvents.collect { event ->
                when (event) {
                    ChooseAppsViewModel.MyWindowEvents.GoBack -> {
                        findNavController().popBackStack()
                        true
                    }
                    is ChooseAppsViewModel.MyWindowEvents.NotifyAdapter -> {
                        blockedAppsAdapter.submitList(event.apps)
                        blockedAppsAdapter.notifyDataSetChanged()
                        true
                    }
                }.exhaustive
            }
        }


        viewModel.installedApps.observe(viewLifecycleOwner) { list ->
            blockedAppsAdapter.submitList(list)

            //blockedAppsAdapter.notifyDataSetChanged()
        }


        /*viewModel.isActive.asLiveData().observe(viewLifecycleOwner) {
            //viewModel.onActiveStateChanges(requireContext())
        }*/

    }

    override fun onItemClicked(app: App, isChecked: Boolean) {
        viewModel.onAppSelected(app, isChecked)
    }
}

