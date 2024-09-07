package com.cyber.restory.presentation.event.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.data.model.GreenTourItem
import com.cyber.restory.data.model.LocationBasedTourItem
import com.cyber.restory.databinding.ItemEventBannerBinding

sealed class TourItem {
    data class GreenTour(val item: GreenTourItem) : TourItem()
    data class LocationBasedTour(val item: LocationBasedTourItem) : TourItem()
}

class TourItemAdapter : ListAdapter<TourItem, TourItemAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemEventBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tourItem: TourItem) {
            binding.apply {
                when (tourItem) {
                    is TourItem.GreenTour -> bindGreenTour(tourItem.item)
                    is TourItem.LocationBasedTour -> bindLocationBasedTour(tourItem.item)
                }
            }
        }

        private fun bindGreenTour(item: GreenTourItem) {
            binding.apply {
                tvEventBannerTitle.text = item.title
                tvEventBannerSubtitle.text = item.summary
                Glide.with(itemView.context)
                    .load(item.mainimage)
                    .centerCrop()
                    .into(ivEventBannerImage)

                root.setOnClickListener {
                    val url = extractUrlFromSummary(item.summary)
                    url?.let { openUrl(it) }
                }
            }
        }

        private fun bindLocationBasedTour(item: LocationBasedTourItem) {
            binding.apply {
                tvEventBannerTitle.text = item.title
                tvEventBannerSubtitle.text = "${item.addr1} ${item.addr2}"
                Glide.with(itemView.context)
                    .load(item.firstimage)
                    .centerCrop()
                    .into(ivEventBannerImage)

                root.setOnClickListener {
                    // LocationBasedTourItem에는 URL이 없으므로, 다른 동작을 수행하거나 아무 동작도 하지 않습니다.
                    // 예를 들어, 상세 정보 화면으로 이동하는 등의 동작을 추가할 수 있습니다.
                }
            }
        }

        private fun extractUrlFromSummary(summary: String): String? {
            val regex = "(http|https)://[a-zA-Z0-9./?=_-]+".toRegex()
            return regex.find(summary)?.value
        }

        private fun openUrl(url: String) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            itemView.context.startActivity(intent)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<TourItem>() {
        override fun areItemsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {
            return when {
                oldItem is TourItem.GreenTour && newItem is TourItem.GreenTour ->
                    oldItem.item.contentid == newItem.item.contentid
                oldItem is TourItem.LocationBasedTour && newItem is TourItem.LocationBasedTour ->
                    oldItem.item.contentid == newItem.item.contentid
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {
            return oldItem == newItem
        }
    }
}