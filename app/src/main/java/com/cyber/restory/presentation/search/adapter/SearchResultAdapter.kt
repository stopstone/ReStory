package com.cyber.restory.presentation.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.R
import com.cyber.restory.data.model.Post
import com.cyber.restory.databinding.ItemPostListBinding

class SearchResultAdapter(
    private val onItemClick: (Post) -> Unit
) : ListAdapter<Post, SearchResultAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPostListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                tvPostTitle.text = post.title
                tvAddress.text = post.address
                tvType.text = post.typeDesc
                tvDistance.visibility = View.GONE

                if (post.postImages.isNotEmpty()) {
                    Glide.with(ivPostImage)
                        .load(post.postImages[0].imageUrl)
                        .placeholder(R.drawable.home_article_image)
                        .error(R.drawable.home_article_image)
                        .into(ivPostImage)
                } else {
                    ivPostImage.setImageResource(R.drawable.home_article_image)
                }

                val typeImageMap = mapOf(
                    "CAFE" to R.drawable.ic_tag_coffee,
                    "EXPERIENCE" to R.drawable.ic_tag_experience,
                    "CULTURE" to R.drawable.ic_tag_culture,
                    "STAY" to R.drawable.ic_tag_stay
                )

                typeImageMap[post.type]?.let {
                    ivType.setImageResource(it)
                }
                // Set click listener for the entire item
                root.setOnClickListener { onItemClick(post) }
            }
        }
    }
}

// PostDiffCallback remains unchanged
class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}