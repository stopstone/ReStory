package com.cyber.restory.presentation.event

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyber.restory.R
import com.cyber.restory.databinding.FragmentEventBannerBinding
import com.cyber.restory.presentation.custom.Region
import com.cyber.restory.presentation.event.adapter.RegionAdapter
import com.cyber.restory.presentation.event.adapter.TourItemAdapter
import com.cyber.restory.presentation.event.viewModel.EventBannerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventBannerFragment : Fragment() {
    private var _binding: FragmentEventBannerBinding? = null
    private val binding get() = _binding!!
    private val regionAdapter: RegionAdapter by lazy { RegionAdapter(::onRegionSelected) }
    private val tourItemAdapter: TourItemAdapter by lazy { TourItemAdapter() }
    private val viewModel: EventBannerViewModel by viewModels()
    private val args: EventBannerFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("EventBannerFragment", "onCreateView 호출")
        _binding = FragmentEventBannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("EventBannerFragment", "onViewCreated 시작")
        setLayout()
        observeViewModel()
        setupBackButton()

        viewModel.initializeWithSeoul(args.bannerPosition)

        when (args.bannerPosition) {
            0 -> Log.d("EventBannerFragment", "첫 번째 배너 클릭 (녹색 관광)")
            1 -> Log.d("EventBannerFragment", "두 번째 배너 클릭 (위치 기반 관광)")
            2 -> Log.d("EventBannerFragment", "세 번째 배너 클릭 (축제공연행사)")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("EventBannerFragment", "onDestroyView 호출")
    }

    private fun setLayout() {
        Log.d("EventBannerFragment", "레이아웃 설정 시작")
        with(binding) {
            setEventBannerRecyclerView()
            setRegionRecyclerView()
            btnRegionSelector.text = "지역 목록"
            btnRegionSelector.setOnClickListener { toggleRegionList() }
            rvRegionList.visibility = View.GONE
        }
        Log.d("EventBannerFragment", "레이아웃 설정 완료")
    }

    private fun setEventBannerRecyclerView() {
        Log.d("EventBannerFragment", "이벤트 배너 RecyclerView 설정 시작")
        with(binding.rvEventBannerList) {
            adapter = tourItemAdapter
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
        }
        Log.d("EventBannerFragment", "이벤트 배너 RecyclerView 설정 완료")
    }

    private fun setRegionRecyclerView() {
        Log.d("EventBannerFragment", "지역 RecyclerView 설정 시작")
        with(binding.rvRegionList) {
            adapter = regionAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
        }
        Log.d("EventBannerFragment", "지역 RecyclerView 설정 완료")
    }

    private fun createColoredRegionText(region: String): SpannableString {
        val fullText = "${region}의 재생공간과\n함께하는 힐링 여행"
        val spannableString = SpannableString(fullText)
        val startIndex = 0
        val endIndex = region.length
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue_500)),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        Log.d("EventBannerFragment", "색상이 적용된 지역 텍스트 생성: $fullText")
        return spannableString
    }

    private fun observeViewModel() {
        Log.d("EventBannerFragment", "ViewModel 관찰 시작")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cityFilters.collect { regions ->
                        Log.d("EventBannerFragment", "새로운 지역 목록 수신: ${regions.size}개의 지역")
                        regionAdapter.submitList(regions)
                    }
                }

                launch {
                    viewModel.selectedRegion.collect { region ->
                        region?.let {
                            Log.d("EventBannerFragment", "선택된 지역 변경: ${it.name}")
                            binding.tvEventBannerTitle.text = createColoredRegionText(it.name)
                            regionAdapter.setSelectedRegion(it)
                        }
                    }
                }

                launch {
                    viewModel.tourItems.collect { items ->
                        Log.d("EventBannerFragment", "관광 정보 수신: ${items.size}개 항목")
                        tourItemAdapter.submitList(items)
                    }
                }
            }
        }
        Log.d("EventBannerFragment", "ViewModel 관찰 설정 완료")
    }


    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            Log.d("EventBannerFragment", "백 버튼 클릭")
            findNavController().navigateUp()
        }
    }

    private fun toggleRegionList() {
        binding.rvRegionList.visibility = if (binding.rvRegionList.visibility == View.VISIBLE) {
            Log.d("EventBannerFragment", "지역 목록 숨김")
            View.GONE
        } else {
            Log.d("EventBannerFragment", "지역 목록 표시")
            View.VISIBLE
        }
    }

    private fun onRegionSelected(region: Region) {
        toggleRegionList()
        Log.d("EventBannerFragment", "선택된 지역: ${region.name}")
        viewModel.setSelectedRegion(region)
    }
}