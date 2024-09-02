package com.cyber.restory.presentation.event.adapter

import android.graphics.Insets.add
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.databinding.ItemEventBannerBinding

class EventBannerAdapter : RecyclerView.Adapter<EventBannerAdapter.EventBannerViewHolder>() {
    private val items: MutableList<String> = mutableListOf<String>().apply { 
        repeat(10) {
            add("item $it")
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventBannerViewHolder {
        return EventBannerViewHolder(
            ItemEventBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EventBannerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class EventBannerViewHolder(binding: ItemEventBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(s: String) {

        }

    }
}