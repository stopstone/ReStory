package com.cyber.restory.presentation.search

import android.content.Context
import android.os.Bundle
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
        showKeyboard()
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

    fun onTagClick(tagName: String) {
        setSearchQuery(tagName)
        performSearch()
    }

    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.searchPosts(query)
//            showSearchResultFragment()
            hideKeyboard()
        }
    }

    private fun setSearchQuery(query: String) {
        binding.etSearch.setText(query)
        binding.etSearch.setSelection(query.length)
    }

    private fun showRecentSearchFragment() {
        supportFragmentManager.commit {
            replace(binding.fragmentContainer.id, RecentSearchFragment.newInstance())
        }
    }

    private fun showSearchResultFragment() {
        supportFragmentManager.commit {
            replace(binding.fragmentContainer.id, ResultSearchFragment.newInstance())
            addToBackStack(null)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    fun onRecentSearchClick(recentSearch: RecentSearch) {
        setSearchQuery(recentSearch.query)
        performSearch()
    }

    fun showKeyboard() {
        val view = binding.etSearch
        view.requestFocus()
        view.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }
}
