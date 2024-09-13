package com.cyber.restory.adapter.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.App
import com.cyber.restory.R
import com.cyber.restory.data.model.postType.FilterType
import com.cyber.restory.databinding.ItemFilterListBinding

class PlaceFilterAdapter() : RecyclerView.Adapter<PlaceFilterAdapter.FilterItemViewHolder>() {
    private var filterList: List<FilterType> = listOf()
    private lateinit var filterClickListener: (FilterType) -> Unit

    inner class FilterItemViewHolder(
        val binding: ItemFilterListBinding,
        val filterClickListener: (FilterType) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: FilterType) = with(binding) {

            val (bgResId, textColorResId) = if (data.isSelected) {
                R.drawable.bg_0066ff_round30 to R.color.color_0066ff
            } else {
                R.drawable.bg_d9d9d9_white_round30 to R.color.color_b1b1b1
            }

            filterNameTextView.apply {
                text = data.description
                background = ResourcesCompat.getDrawable(App.instance.resources, bgResId, null)
                setTextColor(ContextCompat.getColor(App.instance, textColorResId))
            }
        }

        fun bindViews(data: FilterType) = with(binding){
            filterNameTextView.setOnClickListener {
                filterClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterItemViewHolder {
        val view = ItemFilterListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterItemViewHolder(view, filterClickListener)
    }

    override fun getItemCount(): Int = filterList.size

    override fun onBindViewHolder(holder: FilterItemViewHolder, position: Int) {
        holder.bindData(filterList[position])
        holder.bindViews(filterList[position])
    }

    fun setFilterList(filterList: List<FilterType>,
                      filterClickListener: (FilterType) -> Unit) {
        this.filterList = filterList
        this.filterClickListener = filterClickListener
        notifyDataSetChanged()
    }
}