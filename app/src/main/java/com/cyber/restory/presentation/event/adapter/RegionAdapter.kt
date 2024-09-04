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

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class RegionViewHolder(private val binding: ItemRegionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(region: Region, isSelected: Boolean) {
            binding.tvRegionName.text = region.name
            binding.tvRegionName.isSelected = isSelected
            itemView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                onItemClick(region)
            }
        }
    }

    fun setSelectedRegion(region: Region) {
        val position = currentList.indexOfFirst { it.code == region.code }
        if (position != RecyclerView.NO_POSITION) {
            val previousSelected = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
        }
    }
}

class RegionDiffCallback : DiffUtil.ItemCallback<Region>() {
    override fun areItemsTheSame(oldItem: Region, newItem: Region): Boolean = oldItem.code == newItem.code
    override fun areContentsTheSame(oldItem: Region, newItem: Region): Boolean = oldItem == newItem
}