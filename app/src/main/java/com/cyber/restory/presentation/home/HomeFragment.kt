package com.cyber.restory.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cyber.restory.R
import com.cyber.restory.data.model.FilterTypeResponse
import com.cyber.restory.data.model.Post
import com.cyber.restory.databinding.FragmentHomeBinding
import com.cyber.restory.presentation.home.adapter.ArticleThumbnailAdapter
import com.cyber.restory.presentation.home.adapter.BannerAdapter
import com.cyber.restory.presentation.home.viewmodel.HomeViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private val thumbnailAdapter: ArticleThumbnailAdapter by lazy {
        ArticleThumbnailAdapter { post ->
            updateMainThumbnail(post)
        }
    }

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
        setupBannerAdapter()
        setupArticleThumbnailAdapter()

        Log.d("HomeFragment", "onViewCreated 시작")

        setupObservers()
        viewModel.getFilterTypes()

        binding.itemArticle.root.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToArticle()
            findNavController().navigate(action)
        }
    }

    private fun setupBannerAdapter() {
        val bannerAdapter = BannerAdapter { position ->
            Log.d("HomeFragment", "배너 클릭: ${position + 1}번째 배너")
            val action = HomeFragmentDirections.actionHomeToEventBanner(position)
            findNavController().navigate(action)
        }
        binding.viewPagerHomeBanner.adapter = bannerAdapter
    }

    private fun setupArticleThumbnailAdapter() {
        binding.rvHomeArticleThumbnailList.apply {
            adapter = thumbnailAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.filterTypes.collect { types ->
                        createFilterChips(types)
                    }
                }
                launch {
                    viewModel.selectedFilterType.collect { selectedType ->
                        updateSelectedChip(selectedType)
                    }
                }
                launch {
                    viewModel.posts.collect { posts ->
                        Log.d("HomeFragment", "포스트 업데이트: ${posts.size}개의 포스트")
                        updatePostsUI(posts)
                    }
                }

                launch {
                    viewModel.currentThumbnailPost.collect { post ->
                        post?.let {
                            updateMainThumbnail(it)
                        }
                    }
                }
            }
        }
    }

    private fun updateSelectedChip(selectedType: FilterTypeResponse?) {
        binding.cgHomeCategory.children.forEach { chip ->
            (chip as? Chip)?.let {
                it.isChecked = it.text == selectedType?.description
            }
        }
    }

    private fun createFilterChips(filterTypes: List<FilterTypeResponse>) {
        Log.d("HomeFragment", "필터 칩 생성 시작")
        binding.cgHomeCategory.removeAllViews() // 기존 칩 제거

        filterTypes.forEach { filterType ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = filterType.description
                isCheckable = true
                isCheckedIconVisible = false // 체크 아이콘 숨기기

                // 아이콘 제거
                chipIcon = null
                iconEndPadding = 0f
                iconStartPadding = 0f

                // 스타일 적용
                setTextAppearanceResource(R.style.Colors_Widget_MaterialComponents_Chip_Choice)

                // 배경 및 테두리 설정
                chipBackgroundColor = ContextCompat.getColorStateList(
                    context,
                    R.color.color_choice_chip_background_color
                )
                chipStrokeColor = ContextCompat.getColorStateList(
                    context,
                    R.color.color_choice_chip_strokecolor_selector
                )
                chipStrokeWidth = resources.getDimension(R.dimen.chip_stroke_width)

                // 마진 설정
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.chip_margin_end)
                }

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        Log.d("HomeFragment", "${filterType.description} 칩 선택됨")
                        viewModel.selectFilterType(filterType)
                    }
                }
            }

            // 디버그: 칩 텍스트 확인
            Log.d("HomeFragment", "생성된 칩 텍스트: ${chip.text}")

            binding.cgHomeCategory.addView(chip)
        }
        Log.d("HomeFragment", "필터 칩 생성 완료: ${filterTypes.size}개의 칩")
    }

    private fun updatePostsUI(posts: List<Post>) {
        Log.d("HomeFragment", "포스트 UI 업데이트 시작")
        if (posts.isNotEmpty()) {
            val firstPost = posts.first()
            viewModel.setCurrentThumbnailPost(firstPost)

            // 썸네일 리스트 업데이트
            thumbnailAdapter.submitList(posts)
        } else {
            Toast.makeText(requireContext(), "포스트가 없습니다.", Toast.LENGTH_SHORT).show()
        }
        Log.d("HomeFragment", "포스트 UI 업데이트 완료")
    }

    private fun updateMainThumbnail(post: Post) {
        Glide.with(this@HomeFragment)
            .load(post.postImages.firstOrNull()?.imageUrl)
            .centerCrop()
            .into(binding.ivHomeArticleThumbnail)

        binding.tvHomeThumbnailTitle.text = post.title
        binding.tvHomeThumbnailSubtitle.text = post.subContent

        binding.ivHomeArticleThumbnail.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToDetail(post.id)
            findNavController().navigate(action)
        }
    }
}