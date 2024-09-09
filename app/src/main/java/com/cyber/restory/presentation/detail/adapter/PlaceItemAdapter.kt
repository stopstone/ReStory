package com.cyber.restory.presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.data.model.LocationBasedTourItem
import com.cyber.restory.databinding.ItemNearbyPlaceBinding

class PlaceItemAdapter : ListAdapter<LocationBasedTourItem, PlaceItemAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(private val binding: ItemNearbyPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationBasedTourItem) {
            binding.tvTitle.text = item.title
            binding.tvDistance.text = formatDistance(item.dist.toDoubleOrNull() ?: 0.0)

            Glide.with(binding.root.context)
                .load(item.firstimage)
                .into(binding.ivPlace)
        }

        private fun formatDistance(distanceInMeters: Double): String {
            return when {
                distanceInMeters < 1000 -> "${distanceInMeters.toInt()}m"
                else -> String.format("%.1fkm", distanceInMeters / 1000)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNearbyPlaceBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LocationBasedTourItem>() {
        override fun areItemsTheSame(oldItem: LocationBasedTourItem, newItem: LocationBasedTourItem): Boolean {
            return oldItem.contentid == newItem.contentid
        }

        override fun areContentsTheSame(oldItem: LocationBasedTourItem, newItem: LocationBasedTourItem): Boolean {
            return oldItem == newItem
        }
    }
}