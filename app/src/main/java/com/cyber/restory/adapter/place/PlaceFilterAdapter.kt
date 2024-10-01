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

            val (bgResId, textColorResId, selectIconResId) = if (data.isSelected) {
                Triple(R.drawable.bg_17191c_round30, R.color.white, R.drawable.ic_filter_coffee_select)
            } else {
                val selectIcon = when(data.code) {
                    "CAFE" -> R.drawable.ic_filter_coffee
                    "EXPERIENCE" -> R.drawable.ic_filter_experience
                    "CULTURE" -> R.drawable.ic_filter_culture
                    "STAY" -> R.drawable.ic_filter_stay
                    else -> R.drawable.ic_filter_coffee
                }
                Triple(R.drawable.bg_d9d9d9_white_round30, R.color.color_17191C, selectIcon)
            }
            filterNameTextView.apply {
                text = data.description
                setTextColor(ContextCompat.getColor(App.instance, textColorResId))
            }
            filterIconImageView.setImageResource(selectIconResId)

            rootLinearLayout.background = ResourcesCompat.getDrawable(App.instance.resources, bgResId, null)
        }

        fun bindViews(data: FilterType) = with(binding){
            rootLinearLayout.setOnClickListener {
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