package com.cyber.restory.presentation.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.cyber.restory.databinding.ViewRegionSelectorBinding
import com.cyber.restory.presentation.event.adapter.RegionAdapter

class RegionSelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: ViewRegionSelectorBinding
    private val adapter: RegionAdapter by lazy { RegionAdapter { region -> onRegionSelected(region) } }
    private var isExpanded = false
    private var onRegionSelectedListener: ((Region) -> Unit)? = null

    init {
        initView()
        binding.btnSelector.setOnClickListener {
            toggleExpanded()
        }
    }

    private fun initView() {
        binding = ViewRegionSelectorBinding.inflate(LayoutInflater.from(context), this, false)
        binding.rvRegions.adapter = adapter
        binding.rvRegions.layoutManager = GridLayoutManager(context, 4)
        setBackgroundColor(Color.WHITE)
    }

    private fun toggleExpanded() {
        isExpanded = !isExpanded
        binding.rvRegions.visibility = if (isExpanded) View.VISIBLE else View.GONE
        binding.btnSelector.text = if (isExpanded) "접기" else "지역 선택"
    }

    private fun onRegionSelected(region: Region) {
        toggleExpanded()
        binding.btnSelector.text = "${region.name} (${region.cnt})"
        onRegionSelectedListener?.invoke(region)
    }
}

data class Region(
    val code: String,
    val name: String,
    val cnt: Int = 0
)