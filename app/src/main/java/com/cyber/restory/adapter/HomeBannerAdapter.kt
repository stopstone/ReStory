package com.cyber.restory.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.R
import com.cyber.restory.databinding.ItemArticleBinding
import com.cyber.restory.domain.model.Banner

class HomeBannerAdapter : RecyclerView.Adapter<HomeBannerAdapter.HomeBannerViewHolder>() {
    private val items = mutableListOf<Banner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        return HomeBannerViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: HomeBannerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
    fun submitList(banner: List<Banner>) {
        items.clear()
        items.addAll(banner)
        notifyDataSetChanged()
    }

    class HomeBannerViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: Banner) {
            with(binding) {
                tvBannerTitle.text = banner.title
                tvBannerSubtitle.text = banner.subtitle
                tvBannerBadge.text = banner.badge.label
                tvBannerBadge.setBackgroundColor(Color.parseColor(banner.badge.backgroundColor))

                Glide.with(root)
                    .load(banner.image)
                    .centerCrop()
                    .into(ivBannerImage)
            }
        }
    }
}