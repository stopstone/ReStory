package com.cyber.restory.presentation.event

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyber.restory.R
import com.cyber.restory.databinding.FragmentEventBannerBinding
import com.cyber.restory.presentation.custom.Region
import com.cyber.restory.presentation.event.adapter.EventBannerAdapter
import com.cyber.restory.presentation.event.adapter.RegionAdapter

class EventBannerFragment : Fragment() {
    private var _binding: FragmentEventBannerBinding? = null
    private val binding get() = _binding!!
    private val eventBannerAdapter: EventBannerAdapter by lazy { EventBannerAdapter() }
    private val regionAdapter: RegionAdapter by lazy { RegionAdapter(::onRegionSelected) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLayout() {
        with(binding) {
            tvEventBannerTitle.text = createColoredRegionText()
            setEventBannerRecyclerView()
            setRegionRecyclerView()
            btnRegionSelector.setOnClickListener { toggleRegionList() }
        }
    }

    private fun setEventBannerRecyclerView() {
        with(binding.rvEventBannerList) {
            adapter = eventBannerAdapter
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setRegionRecyclerView() {
        with(binding.rvRegionList) {
            adapter = regionAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
        }
        regionAdapter.submitList(getRegions())
    }

    private fun createColoredRegionText(region: String = "지역"): SpannableString {
        val fullText = "${region}의 재생공간과\n함께하는 힐링 여행"
        val spannableString = SpannableString(fullText)
        val startIndex = 0
        val endIndex = region.length
        spannableString.setSpan(
            ForegroundColorSpan(getColor(requireContext(), R.color.blue_500)),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun toggleRegionList() {
        binding.rvRegionList.visibility = if (binding.rvRegionList.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun onRegionSelected(region: Region) {
        binding.tvEventBannerTitle.text = createColoredRegionText(region.name)
        toggleRegionList()
        updateEventBanners(region)
    }

    private fun updateEventBanners(region: Region) {
        // 리스트 아이템을 선택 지역으로 변경
    }

    private fun getRegions(): List<Region> {
        return listOf(
            Region("서울", 15), Region("경기", 8), Region("부산", 2), Region("제주", 0),
            Region("거제시", 0), Region("통영시", 0), Region("사천시", 0), Region("밀양시", 0),
            Region("함안군", 0), Region("거창군", 0), Region("창녕군", 0), Region("고성군", 0),
            Region("하동군", 0), Region("함천군", 0), Region("남해군", 0), Region("함양군", 0),
            Region("신청군", 0), Region("의령군", 0)
        )
    }
}

