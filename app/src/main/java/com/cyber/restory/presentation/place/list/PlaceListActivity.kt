package com.cyber.restory.presentation.place.list

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.R
import com.cyber.restory.adapter.place.PlaceFilterAdapter
import com.cyber.restory.databinding.ActivityPlaceListBinding
import com.cyber.restory.presentation.custom.Region
import com.cyber.restory.presentation.detail.DetailActivity
import com.cyber.restory.presentation.event.adapter.RegionAdapter
import com.cyber.restory.presentation.place.list.adapter.PlaceAdapter
import com.cyber.restory.presentation.place.list.viewmodel.PlaceListViewModel
import com.cyber.restory.presentation.search.SearchActivity
import com.cyber.restory.utils.EventObserver
import com.cyber.restory.utils.MapUtils
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaceListActivity: AppCompatActivity() {
    private val binding: ActivityPlaceListBinding by lazy {
        ActivityPlaceListBinding.inflate(layoutInflater)
    }

    private val viewModel: PlaceListViewModel by viewModels()

    private val placePostAdapter = PlaceAdapter()
    private val placeFilterAdapter = PlaceFilterAdapter()
    private val regionAdapter: RegionAdapter by lazy { RegionAdapter(::onRegionSelected) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.init()
        initView()
        observeEvent()
    }

    private fun initView() = with(binding){
        // 검색 아이콘 클릭
        searchImageView.setOnClickListener {
            val intent = Intent(this@PlaceListActivity, SearchActivity::class.java)
            startActivity(intent)
        }
        areaFilterTextView.setOnClickListener {
            toggleRegionList()
        }
        mapImageView.setOnClickListener {
            finish()
        }
        initStandardLocationText()
        setRegionRecyclerView()
    }

    private fun initStandardLocationText() {
        val status = ContextCompat.checkSelfPermission(this@PlaceListActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(status == PackageManager.PERMISSION_GRANTED) {
            if(viewModel.selectedRegion.value?.code == "ALL") {
                binding.standardLocationTextView.text = "현재 위치 중심"
                val fusedLocationProvideClient =
                    LocationServices.getFusedLocationProviderClient(this@PlaceListActivity)
                fusedLocationProvideClient.lastLocation.addOnSuccessListener { success: Location? ->
                    success?.let { location ->
                        val pos = LatLng.from(location.latitude, location.longitude)
                        fetchPlaceDataListener(pos)
                    }
                }
            } else {
                fetchPlaceDataListener()
                getSelectedRegionListener()
            }
        } else {
            fetchPlaceDataListener()
            getSelectedRegionListener()
        }
    }

    private fun fetchPlaceDataListener(pos: LatLng? = null) {
        if(pos != null) {
            viewModel.fetchPlaceData("","", pos)
        } else {
            viewModel.fetchPlaceData()
        }
    }

    private fun getSelectedRegionListener() = viewModel.getSelectedRegion()


    // 지역 필터 RecyclerView InitView
    private fun setRegionRecyclerView() {
        with(binding.regionlist) {
            adapter = regionAdapter
            layoutManager = GridLayoutManager(this@PlaceListActivity, 4)
        }
    }

    private fun observeEvent() {

        lifecycleScope.launch {
            // 재생공간 목록
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.placeList.observe(this@PlaceListActivity) { list ->
                    binding.placeList.apply {
                        layoutManager = LinearLayoutManager(this@PlaceListActivity, RecyclerView.VERTICAL, false)
                        placePostAdapter.setPostList(list) { item ->
                            // 재생공간 상세로 이동
                            val intent = Intent(this@PlaceListActivity, DetailActivity::class.java).apply {
                                putExtra("postId", item.id)
                            }
                            startActivity(intent)
                        }
                        adapter = placePostAdapter
                    }
                }

                viewModel.filterTypeList.observe(this@PlaceListActivity) { list ->
                    binding.filterList.apply {
                        layoutManager = LinearLayoutManager(this@PlaceListActivity, RecyclerView.HORIZONTAL, false)
                        placeFilterAdapter.setFilterList(list) { type ->
                            viewModel.getFilterPlace(type)
                        }
                        adapter = placeFilterAdapter
                    }
                }
                viewModel.cityFilters.collect { regions ->
                    regions.firstOrNull {
                        it.code == "ALL"
                    }.let {
                        binding.areaFilterTextView.text = "${it?.name} (${it?.cnt})"
                    }
                    regionAdapter.submitList(regions)
                }
            }
        }

        viewModel.filterCategoryChangeEvent.observe(this@PlaceListActivity, EventObserver { type ->
            placeFilterAdapter.notifyDataSetChanged()
            val filterType = type?.code ?: "ALL"
            viewModel.fetchPlaceData(viewModel.selectedRegion.value?.code, if (filterType == "ALL") "" else filterType)
        })

        // 선택한 지역
        viewModel.selectedRegion.observe(this@PlaceListActivity) { region ->
            region?.let {
                binding.areaFilterTextView.text = "${it.name} (${it.cnt})"
                binding.standardLocationTextView.text = "${MapUtils.getCityGovernmentBuilding(it.code)} 중심"
                regionAdapter.setSelectedRegion(it)
            }
        }

        viewModel.selectedCityEvent.observe(this@PlaceListActivity, EventObserver { code ->
            binding.standardLocationTextView.text = "${MapUtils.getCityGovernmentBuilding(code)} 중심"
        })
    }

    private fun toggleRegionList() {
        if (binding.regionlist.visibility == View.VISIBLE) {
            binding.areaToggleImageView.setImageResource(R.drawable.ic_below_arrow)
            binding.regionlist.visibility = View.GONE
        } else {
            binding.areaToggleImageView.setImageResource(R.drawable.ic_up_arrow)
            binding.regionlist.visibility = View.VISIBLE
        }
    }
    private fun onRegionSelected(region: Region) {
        toggleRegionList()
        viewModel.setSelectedRegion(region)
    }
}