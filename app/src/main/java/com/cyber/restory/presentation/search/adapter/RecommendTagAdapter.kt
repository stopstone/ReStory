package com.cyber.restory.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.databinding.ItemRecommendTagListBinding
import com.cyber.restory.data.model.Tag

class RecommendTagAdapter(private val onItemClick: (Tag) -> Unit) :
    ListAdapter<Tag, RecommendTagAdapter.ViewHolder>(TagDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendTagListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRecommendTagListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: Tag) {
            binding.tvSearchRecommendTag.text = tag.name
            binding.root.setOnClickListener { onItemClick(tag) }
        }
    }
}

class TagDiffCallback : DiffUtil.ItemCallback<Tag>() {
    override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem == newItem
    }
}