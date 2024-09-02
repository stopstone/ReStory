package com.cyber.restory.presentation.event.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.databinding.ItemRegionBinding
import com.cyber.restory.presentation.custom.Region

class RegionAdapter(private val onItemClick: (Region) -> Unit) :
    ListAdapter<Region, RegionAdapter.RegionViewHolder>(RegionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RegionViewHolder(private val binding: ItemRegionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(region: Region) {
            binding.tvRegionName.text = region.name
            if (region.count > 0) {
                binding.tvRegionCount.visibility = View.VISIBLE
                binding.tvRegionCount.text = "(${region.count})"
            } else {
                binding.tvRegionCount.visibility = View.GONE
            }
            itemView.setOnClickListener { onItemClick(region) }
        }
    }
}

class RegionDiffCallback : DiffUtil.ItemCallback<Region>() {
    override fun areItemsTheSame(oldItem: Region, newItem: Region): Boolean = oldItem.name == newItem.name
    override fun areContentsTheSame(oldItem: Region, newItem: Region): Boolean = oldItem == newItem
}