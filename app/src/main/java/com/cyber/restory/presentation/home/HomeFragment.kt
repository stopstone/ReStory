package com.cyber.restory.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cyber.restory.R
import com.cyber.restory.adapter.HomeBannerAdapter
import com.cyber.restory.databinding.FragmentHomeBinding
import com.cyber.restory.domain.model.HomeResponse
import com.cyber.restory.utils.AssetLoader
import com.google.gson.Gson
import kotlin.math.abs

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter: HomeBannerAdapter by lazy { HomeBannerAdapter() }
    private val assetLoader: AssetLoader by lazy { AssetLoader(requireContext().assets) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBanners()
        binding.viewpagerHomeBanner.adapter = adapter

        val result = assetLoader.loadAsset("response.json")
        if(!result.isNullOrEmpty()) {
            val response = Gson().fromJson(result, HomeResponse::class.java)
            response?.let {
                adapter.submitList(it.home.banners)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBanners() {
        with(binding.viewpagerHomeBanner) {
            val screenWidth = resources.displayMetrics.widthPixels
            val pageWidth = resources.getDimension(R.dimen.viewpager_item_width)
            val pageMargin = resources.getDimension(R.dimen.viewpager_item_margin)

            val offset = screenWidth - pageWidth - pageMargin
            setPageTransformer { page, position ->
                page.translationX = position * -offset
            }
            offscreenPageLimit = 2
        }
    }
}