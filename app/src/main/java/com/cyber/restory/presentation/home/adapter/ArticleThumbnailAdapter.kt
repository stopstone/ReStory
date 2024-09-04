package com.cyber.restory.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.PostImage
import com.cyber.restory.databinding.ItemArticleThumbnailBinding

class ArticleThumbnailAdapter(
    private val onItemClick: (Post) -> Unit
) : ListAdapter<Post, ArticleThumbnailAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemArticleThumbnailBinding,
        private val onItemClick: (Post) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            Glide.with(binding.root)
                .load(post.postImages.firstOrNull()?.imageUrl)
                .centerCrop()
                .into(binding.ivArticleThumbnail)

            binding.root.setOnClickListener {
                onItemClick(post)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}