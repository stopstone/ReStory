package com.cyber.restory.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyber.restory.databinding.FragmentRecentSearchBinding
import com.cyber.restory.presentation.search.adapter.SearchHistoryAdapter
import com.cyber.restory.presentation.search.adapter.RecommendTagAdapter
import com.cyber.restory.presentation.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecentSearchFragment : Fragment() {
    private var _binding: FragmentRecentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var recommendTagAdapter: RecommendTagAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RecentSearchFragment", "onCreateView 시작")
        _binding = FragmentRecentSearchBinding.inflate(inflater, container, false)
        Log.d("RecentSearchFragment", "onCreateView 완료: 뷰 바인딩 생성")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RecentSearchFragment", "onViewCreated 시작")
        setupRecyclerViews()
        observeViewModel()
        showKeyboard()

        binding.tvClearAll.setOnClickListener {
            Log.d("RecentSearchFragment", "'전체삭제' 버튼 클릭")
            viewModel.clearAllSearches()
        }
        Log.d("RecentSearchFragment", "onViewCreated 완료")
    }

    private fun setupRecyclerViews() {
        Log.d("RecentSearchFragment", "RecyclerView 설정 시작")
        setupSearchHistoryRecyclerView()
        setupRecommendTagRecyclerView()
        Log.d("RecentSearchFragment", "RecyclerView 설정 완료")
    }

    private fun setupSearchHistoryRecyclerView() {
        Log.d("RecentSearchFragment", "최근 검색어 RecyclerView 설정 시작")
        searchHistoryAdapter = SearchHistoryAdapter(
            onItemClick = { search ->
                Log.d("RecentSearchFragment", "최근 검색어 클릭: '${search.query}'")
                (activity as? SearchActivity)?.onRecentSearchClick(search)
            },
            onDeleteClick = { search ->
                Log.d("RecentSearchFragment", "최근 검색어 삭제 요청: '${search.query}'")
                viewModel.deleteSearch(search)
            }
        )
        binding.rvSearchHistoryList.apply {
            adapter = searchHistoryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        Log.d("RecentSearchFragment", "최근 검색어 RecyclerView 설정 완료")
    }

    private fun setupRecommendTagRecyclerView() {
        Log.d("RecentSearchFragment", "추천 태그 RecyclerView 설정 시작")
        recommendTagAdapter = RecommendTagAdapter { tag ->
            Log.d("RecentSearchFragment", "추천 태그 클릭: ID=${tag.id}, 이름='${tag.name}'")
            viewModel.searchPostsByTag(tag.id)
            (activity as? SearchActivity)?.showSearchResultFragment()
        }
        binding.rvSearchRecommendTagList.apply {
            adapter = recommendTagAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        Log.d("RecentSearchFragment", "추천 태그 RecyclerView 설정 완료")
    }

    private fun observeViewModel() {
        Log.d("RecentSearchFragment", "ViewModel 관찰 시작")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentSearches.collect { searches ->
                Log.d("RecentSearchFragment", "최근 검색어 업데이트: ${searches.size}개의 검색어")
                if (searches.isEmpty()) {
                    Log.d("RecentSearchFragment", "최근 검색어 없음: 빈 상태 표시")
                    binding.tvSearchHistoryLabel.visibility = View.GONE
                    binding.groupRecentSearches.visibility = View.GONE
                    binding.tvClearAll.visibility = View.GONE
                } else {
                    Log.d("RecentSearchFragment", "최근 검색어 있음: 리스트 표시")
                    binding.groupRecentSearches.visibility = View.VISIBLE
                    binding.tvSearchHistoryLabel.visibility = View.VISIBLE
                    binding.tvClearAll.visibility = View.VISIBLE

                    searchHistoryAdapter.submitList(searches)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recommendedTags.collect { tags ->
                Log.d("RecentSearchFragment", "추천 태그 업데이트: ${tags.size}개의 태그")
                recommendTagAdapter.submitList(tags)
            }
        }
        Log.d("RecentSearchFragment", "ViewModel 관찰 설정 완료")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("RecentSearchFragment", "onDestroyView 호출: 뷰 바인딩 해제")
        _binding = null
    }

    private fun showKeyboard() {
        Log.d("RecentSearchFragment", "키보드 표시 요청")
        (activity as? SearchActivity)?.showKeyboard()
    }

    companion object {
        fun newInstance(): RecentSearchFragment {
            Log.d("RecentSearchFragment", "newInstance 호출: 새 인스턴스 생성")
            return RecentSearchFragment()
        }
    }
}