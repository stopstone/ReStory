package com.cyber.restory.presentation.event

import android.content.Context
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

    private lateinit var appContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

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
        observeViewModel()
        setupBackButton()

        viewModel.initializeWithSeoul(args.bannerPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLayout() {
        with(binding) {
            setEventBannerRecyclerView()
            setRegionRecyclerView()
            btnRegionSelector.text = "지역 목록"
            btnRegionSelector.setOnClickListener { toggleRegionList() }
            rvRegionList.visibility = View.GONE
            loadingView.visibility = View.GONE
        }
    }

    private fun setEventBannerRecyclerView() {
        with(binding.rvEventBannerList) {
            adapter = tourItemAdapter
            itemAnimator = null
            layoutManager = LinearLayoutManager(appContext)
        }
    }

    private fun setRegionRecyclerView() {
        with(binding.rvRegionList) {
            adapter = regionAdapter
            layoutManager = GridLayoutManager(appContext, 4)
        }
    }

    private fun createColoredRegionText(region: String): SpannableString {
        val fullText = "${region}의 재생공간과\n함께하는 힐링 여행"
        val spannableString = SpannableString(fullText)
        val startIndex = 0
        val endIndex = region.length
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(appContext, R.color.blue_500)),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun observeViewModel() {
        Log.d("EventBannerFragment", "ViewModel 관찰 시작")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.rvEventBannerList.visibility = if (isLoading) View.GONE else View.VISIBLE
                    }
                }

                launch {
                    viewModel.cityFilters.collect { regions ->
                        regionAdapter.submitList(regions)
                    }
                }

                launch {
                    viewModel.selectedRegion.collect { region ->
                        region?.let {
                            binding.tvEventBannerTitle.text = createColoredRegionText(it.name)
                            regionAdapter.setSelectedRegion(it)
                        }
                    }
                }

                launch {
                    viewModel.tourItems.collect { items ->
                        tourItemAdapter.submitList(items)
                    }
                }
            }
        }
    }


    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun toggleRegionList() {
        binding.rvRegionList.visibility = if (binding.rvRegionList.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun onRegionSelected(region: Region) {
        toggleRegionList()
        viewModel.setSelectedRegion(region)
    }
}