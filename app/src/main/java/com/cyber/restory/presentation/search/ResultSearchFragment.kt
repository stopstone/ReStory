package com.cyber.restory.presentation.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyber.restory.databinding.FragmentSearchResultBinding
import com.cyber.restory.presentation.detail.DetailActivity
import com.cyber.restory.presentation.search.adapter.SearchResultAdapter
import com.cyber.restory.presentation.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultSearchFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var searchResultAdapter: SearchResultAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("ResultSearchFragment", "onCreateView 시작")
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ResultSearchFragment", "onViewCreated 시작")
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        Log.d("ResultSearchFragment", "RecyclerView 설정 시작")
        searchResultAdapter = SearchResultAdapter { post ->
            Log.d("ResultSearchFragment", "포스트 클릭됨: id=${post.id}, 제목='${post.title}'")
            viewModel.onPostItemClick(post.id)
        }
        binding.rvSearchResults.apply {
            adapter = searchResultAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        Log.d("ResultSearchFragment", "RecyclerView 설정 완료")
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { posts ->
                Log.d("ResultSearchFragment", "검색 결과 업데이트: ${posts.size}개의 게시글")
                searchResultAdapter.submitList(posts)
                updateUiVisibility(posts.isEmpty())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedPostDetail.collect { postDetail ->
                postDetail?.let {
                    // 재생공간 상세로 이동
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra("postId", postDetail.id)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun updateUiVisibility(isEmpty: Boolean) {
        if (isEmpty) {
            Log.d("ResultSearchFragment", "검색 결과 없음: 빈 상태 표시")
            binding.rvSearchResults.visibility = View.GONE
            binding.tvNoResults.visibility = View.VISIBLE
        } else {
            Log.d("ResultSearchFragment", "검색 결과 있음: 리스트 표시")
            binding.rvSearchResults.visibility = View.VISIBLE
            binding.tvNoResults.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ResultSearchFragment {
            return ResultSearchFragment()
        }
    }
}