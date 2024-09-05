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
import com.cyber.restory.databinding.ItemEventBannerBinding

class PublicApiAdapter : ListAdapter<GreenTourItem, PublicApiAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemEventBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GreenTourItem) {
            binding.apply {
                tvEventBannerTitle.text = item.title
                tvEventBannerSubtitle.text = item.summary
                Glide.with(itemView.context)
                    .load(item.mainimage)
                    .centerCrop()
                    .into(ivEventBannerImage)

                // 아이템 클릭 시 하이퍼링크를 찾아 브라우저로 이동
                binding.root.setOnClickListener {
                    val url = extractUrlFromSummary(item.summary)
                    url?.let {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(it)
                        }
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }

        // summary에서 하이퍼링크를 추출하는 함수
        private fun extractUrlFromSummary(summary: String): String? {
            val regex = "(http|https)://[a-zA-Z0-9./?=_-]+".toRegex()
            return regex.find(summary)?.value
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<GreenTourItem>() {
        override fun areItemsTheSame(oldItem: GreenTourItem, newItem: GreenTourItem): Boolean {
            return oldItem.contentid == newItem.contentid
        }

        override fun areContentsTheSame(oldItem: GreenTourItem, newItem: GreenTourItem): Boolean {
            return oldItem == newItem
        }
    }
}