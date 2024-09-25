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
import com.cyber.restory.databinding.FragmentSearchResultBinding
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
        searchResultAdapter = SearchResultAdapter()
        binding.rvSearchResults.apply {
            adapter = searchResultAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        Log.d("ResultSearchFragment", "RecyclerView 설정 완료")
    }

    private fun observeViewModel() {
        Log.d("ResultSearchFragment", "ViewModel 관찰 시작")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { posts ->
                Log.d("ResultSearchFragment", "검색 결과 업데이트: ${posts.size}개의 게시글")
                searchResultAdapter.submitList(posts)
                updateUiVisibility(posts.isEmpty())
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
        Log.d("ResultSearchFragment", "onDestroyView 호출: 뷰 바인딩 해제")
        _binding = null
    }

    companion object {
        fun newInstance(): ResultSearchFragment {
            Log.d("ResultSearchFragment", "newInstance 호출: 새 인스턴스 생성")
            return ResultSearchFragment()
        }
    }
}