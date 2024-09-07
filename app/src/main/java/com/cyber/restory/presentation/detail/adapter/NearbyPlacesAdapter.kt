package com.cyber.restory.presentation.detail.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.data.model.NearbyPlaceItem
import com.cyber.restory.databinding.ItemNearbyPlaceListBinding
import com.cyber.restory.databinding.ItemNearbyPlaceTitleBinding

class NearbyPlacesAdapter : ListAdapter<NearbyPlaceItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TITLE -> TitleViewHolder.from(parent)
            VIEW_TYPE_PLACE_LIST -> PlaceListViewHolder.from(parent)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is NearbyPlaceItem.Title -> (holder as TitleViewHolder).bind(item)
            is NearbyPlaceItem.PlaceList -> (holder as PlaceListViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NearbyPlaceItem.Title -> VIEW_TYPE_TITLE
            is NearbyPlaceItem.PlaceList -> VIEW_TYPE_PLACE_LIST
        }
    }

    class TitleViewHolder private constructor(private val binding: ItemNearbyPlaceTitleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NearbyPlaceItem.Title) {
            binding.tvTitle.text = item.title
        }

        companion object {
            fun from(parent: ViewGroup): TitleViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNearbyPlaceTitleBinding.inflate(layoutInflater, parent, false)
                return TitleViewHolder(binding)
            }
        }
    }

    class PlaceListViewHolder private constructor(private val binding: ItemNearbyPlaceListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NearbyPlaceItem.PlaceList) {
            val adapter = PlaceItemAdapter()
            binding.rvPlaces.adapter = adapter
            binding.rvPlaces.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            adapter.submitList(item.places)
        }

        companion object {
            fun from(parent: ViewGroup): PlaceListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNearbyPlaceListBinding.inflate(layoutInflater, parent, false)
                return PlaceListViewHolder(binding)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NearbyPlaceItem>() {
        override fun areItemsTheSame(oldItem: NearbyPlaceItem, newItem: NearbyPlaceItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NearbyPlaceItem, newItem: NearbyPlaceItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_TITLE = 0
        private const val VIEW_TYPE_PLACE_LIST = 1
    }
}