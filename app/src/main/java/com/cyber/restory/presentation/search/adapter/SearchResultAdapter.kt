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

class SearchResultAdapter : ListAdapter<Post, SearchResultAdapter.ViewHolder>(PostDiffCallback()) {

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
                tvType.text = post.type
                // 거리 정보가 없으므로 임시로 숨김
                tvDistance.visibility = View.GONE

                // 이미지 로딩
                if (post.postImages.isNotEmpty()) {
                    Glide.with(ivPostImage)
                        .load(post.postImages[0].imageUrl)
                        .placeholder(R.drawable.home_article_image)
                        .error(R.drawable.home_article_image)
                        .into(ivPostImage)
                } else {
                    ivPostImage.setImageResource(R.drawable.home_article_image)
                }

                // 타입 아이콘 설정 (예시, 실제 로직에 맞게 수정 필요)
                when (post.type) {
                    "식당" -> ivType.setImageResource(R.drawable.ic_mission)
                    // 다른 타입에 대한 아이콘 설정
                    else -> ivType.setImageResource(R.drawable.ic_mission)
                }
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}