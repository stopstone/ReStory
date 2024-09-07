package com.cyber.restory.presentation.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.R
import com.cyber.restory.data.model.Post
import com.cyber.restory.databinding.ItemArticleThumbnailBinding

class ArticleThumbnailAdapter(
    private val onItemClick: (Post, Int) -> Unit,
    private val onItemLongPress: (Post, Int) -> Boolean
) : ListAdapter<Post, ArticleThumbnailAdapter.ViewHolder>(DiffCallback()) {

    var selectedPosition = RecyclerView.NO_POSITION
        set(value) {
            val oldPosition = field
            field = value
            notifyItemChanged(oldPosition)
            notifyItemChanged(value)
            Log.d("ArticleThumbnailAdapter", "선택된 포지션 변경: 이전=$oldPosition, 현재=$value")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class ViewHolder(private val binding: ItemArticleThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedPosition = position
                    onItemClick(getItem(position), position)
                }
            }
            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedPosition = position
                    onItemLongPress(getItem(position), position)
                } else {
                    false
                }
            }
        }

        fun bind(post: Post, isSelected: Boolean) {
            Log.d("ArticleThumbnailAdapter", "아이템 바인딩: 포지션=$adapterPosition, 제목=${post.title}, 선택=$isSelected")

            Glide.with(binding.root)
                .load(post.postImages.firstOrNull()?.imageUrl)
                .placeholder(R.color.gray_300)
                .error(R.color.gray_300)
                .centerCrop()
                .into(binding.ivArticleThumbnail)

            binding.viewBorder.isSelected = isSelected
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}