package com.cyber.restory.presentation.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.commit
import com.cyber.restory.data.model.entity.RecentSearch
import com.cyber.restory.databinding.ActivitySearchBinding
import com.cyber.restory.presentation.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
        showRecentSearchFragment()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            Log.d("SearchActivity", "검색 수행: '$query'")
            viewModel.searchPosts(query)
            showSearchResultFragment()
            hideKeyboard()
        } else {
            Log.d("SearchActivity", "검색어가 비어있음")
        }
    }

    private fun setSearchQuery(query: String) {
        Log.d("SearchActivity", "검색어 설정: '$query'")
        binding.etSearch.setText(query)
        binding.etSearch.setSelection(query.length)
    }

    private fun showRecentSearchFragment() {
        Log.d("SearchActivity", "최근 검색 화면 표시")
        supportFragmentManager.commit {
            replace(binding.fragmentContainer.id, RecentSearchFragment.newInstance())
        }
    }

    fun showSearchResultFragment() {
        Log.d("SearchActivity", "검색 결과 화면으로 전환")
        supportFragmentManager.commit {
            replace(binding.fragmentContainer.id, ResultSearchFragment.newInstance())
            addToBackStack(null)
        }
    }

    private fun hideKeyboard() {
        Log.d("SearchActivity", "키보드 숨기기")
        val imm = getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    fun onRecentSearchClick(recentSearch: RecentSearch) {
        Log.d("SearchActivity", "최근 검색어 클릭: '${recentSearch.query}'")
        setSearchQuery(recentSearch.query)
        performSearch()
    }

    fun showKeyboard() {
        Log.d("SearchActivity", "키보드 표시")
        val view = binding.etSearch
        view.requestFocus()
        view.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }
}