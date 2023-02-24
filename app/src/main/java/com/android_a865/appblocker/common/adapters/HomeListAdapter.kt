package com.android_a865.appblocker.common.adapters


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.appblocker.databinding.AdapterHomeScreenBinding
import com.android_a865.appblocker.feature_home.domain.AppsPackage

class HomeListAdapter(
    private val listener: OnEventListener,
) : ListAdapter<AppsPackage, HomeListAdapter.ViewHolder>(PackageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterHomeScreenBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: AdapterHomeScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                delete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onDeleteClicked(item)
                    }
                }


                start.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onStartClicked(item)
                    }
                }

                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onItemClicked(item)
                    }
                }
            }

        }

        @SuppressLint("SetTextI18n")
        fun bind(pkg: AppsPackage) {
            binding.apply {
                title.text = pkg.name
                content.text = "${pkg.apps.size} App . Block time ${pkg.time} minute"
                delete.isVisible = true
                start.isVisible = true
            }
        }
    }


    class PackageDiffCallback : DiffUtil.ItemCallback<AppsPackage>() {
        override fun areItemsTheSame(oldItem: AppsPackage, newItem: AppsPackage): Boolean {
            return (oldItem.name == newItem.name)
        }

        override fun areContentsTheSame(oldItem: AppsPackage, newItem: AppsPackage): Boolean =
            oldItem == newItem
    }

    interface OnEventListener {
        fun onDeleteClicked(app: AppsPackage)
        fun onStartClicked(app: AppsPackage)
        fun onItemClicked(item: AppsPackage?)
    }
}