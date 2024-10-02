package com.cyber.restory.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyber.restory.R
import com.cyber.restory.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = kotlin.math.abs(verticalOffset) >= appBarLayout.totalScrollRange
            if (isCollapsed) {
                binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                binding.btnShare.setImageResource(R.drawable.ic_share)
            } else {
                binding.toolbar.setNavigationIcon(R.drawable.ic_back_detail)
                binding.btnShare.setImageResource(R.drawable.ic_share_detail)
            }
        }
    }

}