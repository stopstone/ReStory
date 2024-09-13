package com.cyber.restory.presentation.home

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyber.restory.R
import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.postType.FilterTypeResponse
import com.cyber.restory.databinding.FragmentHomeBinding
import com.cyber.restory.presentation.home.adapter.ArticleThumbnailAdapter
import com.cyber.restory.presentation.home.adapter.BannerAdapter
import com.cyber.restory.presentation.home.viewmodel.HomeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.shape.CornerFamily
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var thumbnailAdapter: ArticleThumbnailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated 시작")
        setupBannerAdapter()
        setupArticleThumbnailAdapter()
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
        thumbnailAdapter = ArticleThumbnailAdapter(
            onItemClick = { post, position ->
                Log.d("HomeFragment", "썸네일 클릭: 포지션 $position, 포스트 ${post.title}")
                viewModel.selectThumbnail(position)
            },
            onItemLongPress = { post, position ->
                Log.d("HomeFragment", "썸네일 롱프레스: 포지션 $position, 포스트 ${post.title}")
                viewModel.setLongPressState(true)
                viewModel.selectThumbnail(position)
                true
            }
        )

        binding.rvHomeArticleThumbnailList.apply {
            adapter = thumbnailAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    if (e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_CANCEL) {
                        viewModel.setLongPressState(false)
                        Log.d("HomeFragment", "터치 해제: 롱프레스 상태 false로 설정")
                    }
                    return false
                }
            })
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
                launch {
                    viewModel.selectedThumbnailPosition.collect { position ->
                        Log.d("HomeFragment", "선택된 썸네일 포지션 업데이트: $position")
                        thumbnailAdapter.selectedPosition = position
                        binding.rvHomeArticleThumbnailList.scrollToPosition(position)
                    }
                }
            }
        }
    }

    private fun updateSelectedChip(selectedType: FilterTypeResponse?) {
        binding.cgHomeCategory.children.forEach { chip ->
            (chip as? Chip)?.let {
                it.isChecked = it.text == selectedType?.description
                Log.d("HomeFragment", "칩 업데이트: ${it.text}, 선택됨: ${it.isChecked}")
            }
        }
    }

    private fun createFilterChips(filterTypes: List<FilterTypeResponse>) {
        Log.d("HomeFragment", "필터 칩 생성 시작")
        binding.cgHomeCategory.removeAllViews()

        filterTypes.forEach { filterType ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = filterType.description
                isCheckable = true
                isCheckedIconVisible = false

                chipIcon = null
                iconEndPadding = 0f
                iconStartPadding = 0f

                // 텍스트 스타일 적용
                setTextAppearanceResource(R.style.CustomChipStyle)

                // 배경색과 테두리 설정
                chipBackgroundColor = ContextCompat.getColorStateList(
                    context,
                    R.color.color_choice_chip_background_color
                )
                chipStrokeColor = ContextCompat.getColorStateList(
                    context,
                    R.color.color_choice_chip_strokecolor_selector
                )
                chipStrokeWidth = resources.getDimension(R.dimen.chip_stroke_width)

                // 칩의 모서리 반경 설정
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, 100f) // 원하는 반경 값 설정
                    .build()

                // 마진 설정
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.chip_margin_end)
                }

                // 체크 상태에 따라 데이터 요청
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        Log.d("HomeFragment", "${filterType.description} 칩 선택됨")
                        viewModel.selectFilterType(filterType) // 데이터 요청 로직 유지
                        setTypeface(null, Typeface.BOLD) // 선택된 경우 볼드체로 변경
                    } else {
                        setTypeface(null, Typeface.NORMAL) // 기본은 일반체
                    }
                }
            }

            Log.d("HomeFragment", "생성된 칩 텍스트: ${chip.text}")

            binding.cgHomeCategory.addView(chip)
        }
        Log.d("HomeFragment", "필터 칩 생성 완료: ${filterTypes.size}개의 칩")
    }

    private fun updatePostsUI(posts: List<Post>) {
        Log.d("HomeFragment", "포스트 UI 업데이트 시작")
        if (posts.isNotEmpty()) {
            thumbnailAdapter.submitList(posts) {
                Log.d("HomeFragment", "썸네일 어댑터 리스트 업데이트 완료")
            }
        }
        Log.d("HomeFragment", "포스트 UI 업데이트 완료")
    }

    private fun updateMainThumbnail(post: Post) {
        Log.d("HomeFragment", "메인 썸네일 업데이트: ${post.title}")
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