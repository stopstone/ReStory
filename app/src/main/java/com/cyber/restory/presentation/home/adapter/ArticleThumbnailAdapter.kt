package com.cyber.restory.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.data.model.PostImage
import com.cyber.restory.databinding.ItemArticleThumbnailBinding

class ArticleThumbnailAdapter(
    private val onItemClick: (PostImage) -> Unit
) : ListAdapter<PostImage, ArticleThumbnailAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemArticleThumbnailBinding,
        private val onItemClick: (PostImage) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postImage: PostImage) {
            Glide.with(binding.root)
                .load(postImage.imageUrl)
                .centerCrop()
                .into(binding.ivArticleThumbnail)

            binding.root.setOnClickListener {
                onItemClick(postImage)
            }
        }
    }


    private class DiffCallback : DiffUtil.ItemCallback<PostImage>() {
        override fun areItemsTheSame(oldItem: PostImage, newItem: PostImage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostImage, newItem: PostImage): Boolean {
            return oldItem == newItem
        }
    }
}