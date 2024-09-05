package com.cyber.restory.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.R
import com.cyber.restory.databinding.ItemBannerImageBinding

class BannerAdapter(private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    private val images = listOf(
        R.drawable.banner_image1,
        R.drawable.banner_image2,
        R.drawable.banner_image3
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(images[position], position)
    }

    override fun getItemCount() = images.size

    class BannerViewHolder(
        private val binding: ItemBannerImageBinding,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageRes: Int, position: Int) {
            binding.bannerImageView.setImageResource(imageRes)
            binding.root.setOnClickListener { onItemClick(position) }
        }
    }
}