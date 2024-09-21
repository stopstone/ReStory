package com.cyber.restory.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyber.restory.databinding.FragmentRecentSearchBinding
import com.cyber.restory.presentation.search.adapter.SearchHistoryAdapter
import com.cyber.restory.presentation.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecentSearchFragment : Fragment() {
    private var _binding: FragmentRecentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        showKeyboard()

        binding.tvClearAll.setOnClickListener {
            viewModel.clearAllSearches()
        }
    }

    private fun setupRecyclerView() {
        searchHistoryAdapter = SearchHistoryAdapter(
            onItemClick = { search ->
                (activity as? SearchActivity)?.onRecentSearchClick(search)
            },
            onDeleteClick = { search ->
                viewModel.deleteSearch(search)
            }
        )
        binding.rvSearchHistoryList.apply {
            adapter = searchHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentSearches.collect { searches ->
                if (searches.isEmpty()) {
                    binding.groupRecentSearchesEmpty.visibility = View.VISIBLE
                    binding.groupRecentSearches.visibility = View.GONE
                } else {
                    binding.groupRecentSearches.visibility = View.VISIBLE
                    binding.groupRecentSearchesEmpty.visibility = View.GONE
                    searchHistoryAdapter.submitList(searches)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showKeyboard() {
        (activity as? SearchActivity)?.showKeyboard()
    }

    companion object {
        fun newInstance() = RecentSearchFragment()
    }
}
