package com.android_a865.appblocker


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.appblocker.databinding.AdapterBlockedAppsBinding

class BlockedAppsAdapter() : ListAdapter<App, BlockedAppsAdapter.ViewHolder>(InvoiceDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterBlockedAppsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: AdapterBlockedAppsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(app: App) {
            binding.apply {
                image.setImageDrawable(app.icon)
                appName.text = app.name
            }
        }
    }

    class InvoiceDiffCallback : DiffUtil.ItemCallback<App>() {
        override fun areItemsTheSame(oldItem: App, newItem: App): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: App, newItem: App): Boolean =
            oldItem == newItem
    }
}