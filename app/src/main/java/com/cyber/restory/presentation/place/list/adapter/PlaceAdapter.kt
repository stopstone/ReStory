package com.cyber.restory.presentation.place.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.databinding.ItemPostListBinding
import com.cyber.restory.presentation.place.list.PostItem

class PlaceAdapter() : RecyclerView.Adapter<PlaceAdapter.FilterItemViewHolder>() {
    private var postList: List<PostItem> = listOf()
    private lateinit var postClickListener: (PostItem) -> Unit

    inner class FilterItemViewHolder(
        val binding: ItemPostListBinding,
        val postClickListener: (PostItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: PostItem) = with(binding) {
            Glide.with(itemView.context)
                .load(data.postImages.firstOrNull()?.imageUrl)
                .centerCrop()
                .into(ivPostImage)
            tvPostTitle.text = data.title
            tvAddress.text = data.address

            tvDistance.text = if(data.distance < 1000) {
                "${data.distance}m"
            } else {
                String.format("%.1fkm", data.distance / 1000)
            }

            /*tvDistance.text = MapUtils.getDistance(
                data.latitude,
                data.longitude,
                data.selectedLatitude,
                data.selectedLongitude
            )*/
            tvType.text = data.type


            /**
            TODO : 재생공간 Type 별로 아이콘 분기 처리
             * ivType.
             * */
        }

        fun bindViews(data: PostItem) = with(binding) {
            root.setOnClickListener {
                postClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterItemViewHolder {
        val view = ItemPostListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterItemViewHolder(view, postClickListener)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: FilterItemViewHolder, position: Int) {
        holder.bindData(postList[position])
        holder.bindViews(postList[position])
    }

    fun setPostList(
        postList: List<PostItem>,
        postClickListener: (PostItem) -> Unit
    ) {
        this.postList = postList
        this.postClickListener = postClickListener
        notifyDataSetChanged()
    }
}