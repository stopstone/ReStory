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

        adapter.submitList(getRegions())
    }

    private fun toggleExpanded() {
        isExpanded = !isExpanded
        binding.rvRegions.visibility = if (isExpanded) View.VISIBLE else View.GONE
        binding.btnSelector.text = if (isExpanded) "접기" else "지역 선택"
    }

    private fun onRegionSelected(region: Region) {
        binding.btnSelector.text = region.name
        toggleExpanded()
        Log.d("RegionSelector", "Selected region: ${region.name}")
    }

    private fun getRegions(): List<Region> {
        return listOf(
            Region("서울", 15), Region("경기", 8), Region("부산", 2), Region("제주"),
            Region("거제시"), Region("통영시"), Region("사천시"), Region("밀양시"),
            Region("함안군"), Region("거창군"), Region("창녕군"), Region("고성군"),
            Region("하동군"), Region("함천군"), Region("남해군"), Region("함양군"),
            Region("신청군"), Region("의령군")
        )
    }
}

data class Region(val name: String, val count: Int = 0)