package com.cyber.restory.presentation.home

import android.content.res.ColorStateList
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

                // 필터 타입에 따라 아이콘 설정
                val iconResId = when (filterType.description) {
                    "카페&음식점" -> R.drawable.ic_filter_coffee
                    "체험" -> R.drawable.ic_filter_experience
                    "숙박" -> R.drawable.ic_filter_stay
                    "문화공간" -> R.drawable.ic_filter_culture
                    else -> null
                }

                if (iconResId != null) {
                    val iconTint = ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked)
                        ),
                        intArrayOf(
                            ContextCompat.getColor(context, R.color.white),
                            ContextCompat.getColor(context, R.color.black)
                        )
                    )

                    chipIcon = ContextCompat.getDrawable(context, iconResId)
                    chipIconTint = iconTint
                    iconEndPadding = resources.getDimension(R.dimen.chip_icon_padding)
                    iconStartPadding = resources.getDimension(R.dimen.chip_icon_padding)
                } else {
                    chipIcon = null
                    iconEndPadding = 0f
                    iconStartPadding = 0f
                }

                // 기존 코드 유지
                setTextAppearanceResource(R.style.CustomChipStyle)

                chipBackgroundColor = ContextCompat.getColorStateList(
                    context,
                    R.color.color_choice_chip_background_color
                )
                chipStrokeColor = ContextCompat.getColorStateList(
                    context,
                    R.color.color_choice_chip_strokecolor_selector
                )
                chipStrokeWidth = resources.getDimension(R.dimen.chip_stroke_width)

                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, 100f)
                    .build()

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
                        setTypeface(null, Typeface.BOLD)
                    } else {
                        setTypeface(null, Typeface.NORMAL)
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
        binding.tvHomeThumbnailSubtitle.text = post.summary

        binding.ivHomeArticleThumbnail.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToDetail(post.id)
            findNavController().navigate(action)
        }
    }
}