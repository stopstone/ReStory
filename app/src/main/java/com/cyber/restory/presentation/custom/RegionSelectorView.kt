package com.cyber.restory.presentation.custom

import android.content.Context
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
    private val adapter = RegionAdapter { region ->
        onRegionSelected(region)
    }
    private var isExpanded = false

    init {
        initView()
    }

    private fun initView() {
        binding = ViewRegionSelectorBinding.inflate(LayoutInflater.from(context), this, true)
        binding.rvRegions.adapter = adapter
        binding.rvRegions.layoutManager = GridLayoutManager(context, 4)

        binding.btnSelector.setOnClickListener {
            toggleExpanded()
        }
    }

    private fun toggleExpanded() {
        isExpanded = !isExpanded
        binding.rvRegions.visibility = if (isExpanded) View.VISIBLE else View.GONE
        binding.btnSelector.text = if (isExpanded) "접기" else "지역 선택"
        Log.d("RegionSelectorView", "토글 상태 변경: ${if (isExpanded) "펼침" else "접음"}")
    }

    private fun onRegionSelected(region: Region) {
        binding.btnSelector.text = region.name
        toggleExpanded()
        Log.d("RegionSelectorView", "선택된 지역: ${region.name}")
    }

    fun setRegions(regions: List<Region>) {
        adapter.submitList(regions)
        Log.d("RegionSelectorView", "지역 목록 설정 완료. 총 ${regions.size}개의 지역")
    }
}

data class Region(val code: String, val name: String)