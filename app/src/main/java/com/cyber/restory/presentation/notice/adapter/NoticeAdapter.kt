package com.cyber.restory.presentation.notice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.data.model.Notice
import com.cyber.restory.databinding.ItemNoticeBinding

class NoticeAdapter(private val onItemClick: (Int) -> Unit) :
    ListAdapter<Notice, NoticeViewHolder>(NoticeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class NoticeViewHolder(
    private val binding: ItemNoticeBinding,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onItemClick(adapterPosition)
        }
    }

    fun bind(notice: Notice) {
        binding.apply {
            tvTitle.text = notice.title
            tvDate.text = notice.date
            tvContent.text = notice.content
            tvContent.isVisible = notice.isExpanded
            ivArrow.rotation = if (notice.isExpanded) 180f else 0f
        }
    }
}

class NoticeDiffCallback : DiffUtil.ItemCallback<Notice>() {
    override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
        return oldItem.title == newItem.title && oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean {
        return oldItem == newItem
    }
}