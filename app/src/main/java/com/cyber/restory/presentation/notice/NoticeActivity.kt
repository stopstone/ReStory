package com.cyber.restory.presentation.notice

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyber.restory.databinding.ActivityNoticeBinding
import com.cyber.restory.presentation.notice.adapter.NoticeAdapter
import com.cyber.restory.presentation.notice.viewmodel.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeBinding
    private val viewModel: NoticeViewModel by viewModels()
    private lateinit var adapter: NoticeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeNotices()
        observeError()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarNotice)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupRecyclerView() {
        adapter = NoticeAdapter { position ->
            viewModel.toggleNoticeExpansion(position)
        }
        binding.rvNotices.adapter = adapter
        binding.rvNotices.layoutManager = LinearLayoutManager(this)
    }

    private fun observeNotices() {
        lifecycleScope.launch {
            viewModel.notices.collect { notices ->
                adapter.submitList(notices)
            }
        }
    }

    private fun observeError() {
        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(this@NoticeActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}