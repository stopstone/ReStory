package com.cyber.restory.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.cyber.restory.R
import com.cyber.restory.data.model.Post
import com.cyber.restory.databinding.ActivityDetailBinding
import com.cyber.restory.presentation.detail.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.getPostDetail(args.postId)
        setupClickListeners()

        lifecycleScope.launch {
            viewModel.postDetail.collect { post ->
                if (post != null) {
                    updateUI(post)
                }
            }
        }
    }

    private fun updateUI(post: Post) {
        with(binding) {
            tvArticleDetailCategory.text = post.type
            articleDetailTitle.text = post.title
            tvArticleDetailDescription.text = post.summary
            tvArticleDetailTime.text = post.duration
            tvArticleDetailTimeHoliday.text = "${post.holiday} 휴무"
            tvArticleDetailTelephone.text = post.telephone
            tvArticleDetailHomepage.text = post.url

            if (post.postImages.isNotEmpty()) {
                Glide.with(this@DetailActivity)
                    .load(post.postImages[0].imageUrl)
                    .into(ivArticleDetailImage)

                Glide.with(this@DetailActivity)
                    .load(post.postImages[1].imageUrl)
                    .centerCrop()
                    .into(ivArticleDetailBehindImage)
                tvArticleDetailBehindText.text = post.content
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            ivArticleDetailTimeToggle.setOnClickListener {
                it.isSelected = !it.isSelected
                tvArticleDetailTimeHoliday.visibility =
                    if (it.isSelected) View.VISIBLE else View.INVISIBLE

                val params =
                    tvArticleDetailTelephoneLabel.layoutParams as ConstraintLayout.LayoutParams
                params.topMargin = when (it.isSelected) {
                    true -> resources.getDimensionPixelSize(R.dimen.expanded_margin)
                    false -> resources.getDimensionPixelSize(R.dimen.collapsed_margin)
                }
                tvArticleDetailTelephoneLabel.layoutParams = params
            }

            tvArticleDetailTelephone.setOnClickListener { openDialer(binding.tvArticleDetailTelephone.text.toString()) }
            tvArticleDetailHomepage.setOnClickListener { openWebPage(binding.tvArticleDetailHomepage.text.toString()) }

            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }


    private fun openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    private fun openWebPage(url: String) {
        if (url.isNotEmpty()) {
            val fullUrl =
                if (url.startsWith("http://") || url.startsWith("https://")) url else "http://$url"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
            startActivity(intent)
        }
    }
}