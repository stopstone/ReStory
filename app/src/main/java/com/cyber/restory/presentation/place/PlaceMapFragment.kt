package com.cyber.restory.presentation.place

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyber.restory.App
import com.cyber.restory.R
import com.cyber.restory.adapter.place.PlaceFilterAdapter
import com.cyber.restory.databinding.FragmentPlaceMapBinding
import com.cyber.restory.presentation.custom.Region
import com.cyber.restory.presentation.event.adapter.RegionAdapter
import com.cyber.restory.utils.EventObserver
import com.cyber.restory.utils.MapUtils.Companion.getCoordinate
import com.cyber.restory.utils.ToastUtils
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PlaceMapFragment : Fragment() {
    private var _binding: FragmentPlaceMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaceMapViewModel by viewModels()

    lateinit var kakaoMap: KakaoMap
    private var labelLayer: LabelLayer? = null
    private val ACCESS_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    lateinit var behavior: BottomSheetBehavior<ConstraintLayout>

    private val placeFilterAdapter = PlaceFilterAdapter()

    private val regionAdapter: RegionAdapter by lazy { RegionAdapter(::onRegionSelected) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaceMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()
        observeEvent()
        initKakaoMap()
        initListButtonListener()
        persistentBottomSheetEvent()

    }

    private fun initView() = with(binding) {
        searchImageView.setOnClickListener {
            // TODO : 검색 페이지로 이동
            ToastUtils.showTopToast("검색 페이지로 이동")
        }
        areaFilterTextView.setOnClickListener {
            toggleRegionList()
        }
        setRegionRecyclerView()
    }

    private fun observeEvent() {
        // 재생공간 목록
        viewModel.placeList.observe(viewLifecycleOwner) { list ->
            if (::kakaoMap.isInitialized) {
                // 기존 마커 제거
                labelLayer?.removeAll()
                list.forEachIndexed { index, post ->
                    // 마커 생성
                    showIconLabel(index, post.latitude, post.longitude)
                }
            } else {
                return@observe
            }
        }
        // 선택한 지역
        viewModel.selectedRegion.observe(viewLifecycleOwner) { region ->
            region?.let {
                binding.areaFilterTextView.text = it.name
                regionAdapter.setSelectedRegion(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filterTypeList.observe(viewLifecycleOwner) { list ->
                    binding.filterList.apply {
                        layoutManager = LinearLayoutManager(view?.context, RecyclerView.HORIZONTAL, false)
                        placeFilterAdapter.setFilterList(list) { type ->
                            viewModel.getFilterPlace(type)
                        }
                        adapter = placeFilterAdapter
                    }
                }
                viewModel.setBottomSheetDataEvent.observe(viewLifecycleOwner, EventObserver { data ->
                        if (data.isNotEmpty()) {
                            binding.placeNameTextView.text = data[0].title
                            binding.addressTextView.text = data[0].address
                            binding.typeTextView.text = data[0].type
                            binding.persistentBottomSheet.setOnClickListener {
                                val action = PlaceMapFragmentDirections.actionMapToDetail(data[0].id)
                                findNavController().navigate(action)
                            }
                        }
                    })

                viewModel.filterCategoryChangeEvent.observe(viewLifecycleOwner, EventObserver { type ->
                        placeFilterAdapter.notifyDataSetChanged()
                        val filterType = type?.code ?: "ALL"
                        viewModel.fetchPlaceData(viewModel.selectedRegion.value?.code, if (filterType == "ALL") "" else filterType)
                })
                viewModel.cityFilters.collect { regions ->
                    regionAdapter.submitList(regions)
                }
            }
        }
    }

    private fun createListButtonLayoutParams(value: Int) : FrameLayout.LayoutParams{
        val dm = resources.displayMetrics
        val size = Math.round(value * dm.density)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        param.bottomMargin = size

        return param
    }
    private fun persistentBottomSheetEvent() {
        behavior = BottomSheetBehavior.from(binding.persistentBottomSheet)
        behavior.apply {
            // BottomSheetDialog는 아래로 숨겨진 상태
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            binding.listButton.layoutParams = createListButtonLayoutParams(160)
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {}
                        BottomSheetBehavior.STATE_EXPANDED -> behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.listButton.layoutParams = createListButtonLayoutParams(100)
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {}
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
                    }
                }
                override fun onSlide(p0: View, p1: Float) {}
            })
        }
    }

    private fun initListButtonListener() {
        binding.listButton.setOnClickListener {
            // TODO : 리스트 모드로 전환
            ToastUtils.showToast("리스트 모드 준비중 입니다.")
        }
    }

    // 지역 필터 RecyclerView InitView
    private fun setRegionRecyclerView() {
        with(binding.rvRegionList) {
            adapter = regionAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
        }
    }

    /*
    * KakaoMap Init
    * */
    private fun initKakaoMap() {
        binding.mapView.start(object : MapLifeCycleCallback() {
            // 지도 API 가 정상적으로 종료될 때 호출됨
            override fun onMapDestroy() {}
            // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            override fun onMapError(error: Exception) {}
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                kakaoMap = map
                kakaoMap.cameraMinLevel = 9
                kakaoMap.cameraMaxLevel = 17
                labelLayer = kakaoMap.labelManager?.layer
                labelClickEvent()
                setupLocationPermission()
                viewModel.init()
            }

            // 지도 시작 시 확대/축소 줌 레벨 설정
            override fun getZoomLevel() = 12

            // 서울시청 좌표로 시작 지점 설정
            override fun getPosition() = LatLng.from(37.56677014292466, 126.97865227425055)
        })
    }

    /*
    * KakaoMap에 재생공간 Marker 생성
    * */
    private fun showIconLabel(index: Int, lat: Double, lon: Double) {
        val pos = LatLng.from(lat, lon)
        // TODO : 재생공간 타입별 마커 아이콘 분기 처리
        val styles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.ic_marker)
                    .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
            )
        )
        // 라벨 생성
        labelLayer?.addLabel(LabelOptions.from("iconLabel_$index", pos).setStyles(styles))
    }

    /*
    * 재생공간 Marker Click Event
    * */
    fun labelClickEvent() {
        kakaoMap.isPoiClickable = true
        kakaoMap.setOnLabelClickListener { map, layer, label ->
            viewModel.getSelectPlaceData(label.position.latitude, label.position.longitude)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            true
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun setupLocationPermission() {
        val fusedLocationProvideClient =
            LocationServices.getFusedLocationProviderClient(App.instance)
        if (ActivityCompat.checkSelfPermission(
                view?.context ?: App.instance,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                view?.context ?: App.instance,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@PlaceMapFragment.activity!!,
                ACCESS_PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationProvideClient.lastLocation.addOnSuccessListener { success: Location? ->
            success?.let { location ->
                /*
                * 좌표 location.latitude, location.longitude
                * viewModel.getPlace(location.latitude, location.longitude)
                * */

                //
                /*val pos = LatLng.from(location.latitude, location.longitude)
                // 라벨 스타일 생성
                val styles = kakaoMap.labelManager
                    ?.addLabelStyles(
                        LabelStyles.from(
                            LabelStyle.from(R.drawable.ic_marker)
                                .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                        )
                    )


                // 라벨 생성
                labelLayer?.addLabel(LabelOptions.from("iconLabel22", pos).setStyles(styles))

                kakaoMap.moveCamera( CameraUpdateFactory.newCenterPosition(pos))*/

                /*kakaoMap.moveCamera(
                    CameraUpdateFactory.newCenterPosition(pos),
                    //CameraUpdateFactory.newCenterPosition(pos, 15),
                    CameraAnimation.from(duration)
                )*/
                //
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showToast("위치 권한이 동의 되었습니다.")
            } else {
                ToastUtils.showToast("권한에 동의하지 않을 경우 서비스 이용이 제한됩니다.")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // KakaoMapView의 resume 호출
        binding.mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        // KakaoMapView의 pause 호출
        binding.mapView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleRegionList() {
        binding.rvRegionList.visibility = if (binding.rvRegionList.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun onRegionSelected(region: Region) {
        toggleRegionList()
        viewModel.setSelectedRegion(region)
        setKakaoMapPosition(region)
    }

    private fun setKakaoMapPosition(region: Region) {
        val pos = LatLng.from(getCoordinate(region).first, getCoordinate(region).second )
        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(pos))
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

}