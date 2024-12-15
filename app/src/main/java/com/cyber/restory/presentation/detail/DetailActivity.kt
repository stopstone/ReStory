package com.cyber.restory.presentation.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cyber.restory.R
import com.cyber.restory.data.model.Post
import com.cyber.restory.databinding.ActivityDetailBinding
import com.cyber.restory.presentation.detail.adapter.NearbyPlacesAdapter
import com.cyber.restory.presentation.detail.viewmodel.DetailViewModel
import com.cyber.restory.utils.loadImage
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val binding: ActivityDetailBinding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private var mapView: MapView? = null
    private var kakaoMap: KakaoMap? = null

    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailActivityArgs by navArgs()

    private val nearbyPlacesAdapter = NearbyPlacesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupClickListeners()
        setupRecyclerView()

        // 즉시 ViewModel에 게시글 ID 전달
        viewModel.initializeWithPostId(args.postId)

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        mapView?.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.finish()
    }

    private fun setupRecyclerView() {
        binding.rvNearbyPlaces.adapter = nearbyPlacesAdapter
        binding.rvNearbyPlaces.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.postDetail.collect { post ->
                if (post != null) {
                    updateUI(post)
                    initializeMapView(post)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.nearbyPlaces.collect { places ->
                nearbyPlacesAdapter.submitList(places)
            }
        }
    }

    private fun updateUI(post: Post) {
        with(binding) {
            toolbar.title = post.title
            tvArticleDetailCategory.text = post.typeDesc
            articleDetailTitle.text = post.title
            tvArticleDetailDescription.text = post.content
            tvArticleDetailTime.text = post.duration
            tvArticleDetailTimeHoliday.text = "${post.holiday} 휴무"
            tvArticleDetailTelephone.text = post.telephone
            tvArticleDetailHomepage.text = post.url
            tvArticleDetailMapText.text = post.address
            tvArticleDetailBehindText.text = post.subContent

            if (post.postImages.isNotEmpty()) {
                ivArticleDetailImage.loadImage(post.postImages[0].imageUrl)
                ivArticleDetailBehindImage.loadImage(post.postImages[0].imageUrl)
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

            toolbar.setNavigationOnClickListener { finish() }

            btnArticleDetailMapCopy.setOnClickListener {
                copyTextToClipboard(tvArticleDetailMapText.text.toString())
            }
        }
    }

    private fun openDialer(phoneNumber: String) {
        Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            startActivity(this)
        }
    }

    private fun openWebPage(url: String) {
        if (url.isNotEmpty()) {
            val fullUrl =
                if (url.startsWith("http://") || url.startsWith("https://")) url else "http://$url"
            Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl)).apply {
                startActivity(this)
            }
        }
    }

    private fun initializeMapView(post: Post) {
        mapView = binding.mapView
        mapView?.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
            }

            override fun onMapError(error: Exception) {
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                showLocationOnMap(post)
            }
        })
    }

    private fun showLocationOnMap(post: Post) {
        val location = LatLng.from(post.latitude, post.longitude)

        kakaoMap?.let { map ->
            map.moveCamera(CameraUpdateFactory.newCenterPosition(location))
            map.moveCamera(CameraUpdateFactory.zoomTo(16))
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "클립보드에 저장 되었습니다.", Toast.LENGTH_SHORT).show()
    }
}