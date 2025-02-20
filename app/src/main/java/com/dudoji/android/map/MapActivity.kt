package com.dudoji.android.map

import android.location.Location
import android.os.Bundle
import com.dudoji.android.MainActivity
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.model.mapsection.MapSectionManager
import com.dudoji.android.util.MapUtil
import com.dudoji.android.util.location.LocationService
import com.dudoji.android.util.tile.MaskTileProvider
import com.dudoji.android.util.tile.mask.IMaskTileMaker
import com.dudoji.android.util.tile.mask.MapSectionMaskTileMaker
import com.dudoji.android.util.tile.mask.PositionsMaskTileMaker
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

const val MIN_ZOOM = 10f
const val MAX_ZOOM = 20f

class MapActivity :  NavigatableActivity(), OnMapReadyCallback {

    override val navigationItems = mapOf(
        R.id.mapFragment to null, // 기본 맵 화면
        R.id.homeFragment to MainActivity::class.java,
//        R.id.locationFragment to LocationActivity::class.java
    )

    override val defaultSelectedItemId = R.id.mapFragment

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var locationService: LocationService //로케이션 서비스 변수 추가

    var googleMap: GoogleMap? = null
    var mapUtil: MapUtil = MapUtil(this)

    fun setTileMaskTileMaker(maskTileMaker: IMaskTileMaker) {
        val tileOverlayOptions = TileOverlayOptions().tileProvider(MaskTileProvider(maskTileMaker))
        googleMap?.addTileOverlay(tileOverlayOptions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapUtil.setupLocationServices()
        mapUtil.requestLocationPermission()
        mapUtil.prepareMap()

        bottomNav = findViewById(R.id.navigationView)

        bottomNav.selectedItemId = R.id.mapFragment

        setupBottomNavigation(findViewById(R.id.navigationView))

        locationService = LocationService(this)

        // TODO - Location Service를 초기화 변수로 저장해놓음
        setupLocationUpdates() // 위치 업데이트 콜백 설정 함수
        // 그리고 CallBack 등록
        // 아래 형식으로 인자에 넣어주면 될듯 //
//        호출해야하는함수(object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult.lastLocation?.let {
//                    updateLocationLog(it) // TODO 안에 로직도 수정, RevealCircleRepository에 등록
                                            // TODO Google Map에 위치 수정
//                    RevealCircleRepository.addLocation(it)
//                }
//            }
//        })
    }


    private fun setupLocationUpdates(){
        locationService.setLocationCallback(object: LocationCallback() {//로케이션 서비스 콜백 설정 함수
            override fun onLocationResult(locationResult: LocationResult?){
                locationResult?.lastLocation?.let{
                    updateLocationOnMap(it)
                }
            }

        })
    }

    //구글맵에 위치 업뎃
    private fun updateLocationOnMap(location: Location){
        // getLastLatLng()를 사용하여 마지막 위치를 가져옵니다.
        val (latitude, longitude) = locationService.getLastLatLng()

        // 마지막 위치가 유효한지 확인하고, 유효하면 지도에 위치를 업데이트합니다.
        if (latitude != 0.0 && longitude != 0.0) {
            val latLng = LatLng(latitude, longitude) // LatLng 객체로 변환
            googleMap?.clear() // 이전 마커 제거
            googleMap?.addMarker(MarkerOptions().position(latLng).title("Last Known Location")) // 구글맵에 마커 추가
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)) // 카메라 이동크기는 임의로 잡아봄 몇이누?
        } else {
            // 위치 정보가 없을 경우 처리 (예: 기본 위치 표시)
            val defaultLatLng = LatLng( 35.23225274, 129.08211597) // 부산대 위치 소수점 8자리까지 제출해봄
            googleMap?.clear()  // 이전 마커 제거
            googleMap?.addMarker(MarkerOptions().position(defaultLatLng).title("Default Location")) // 구글맵에 마커 추가
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15f)) // 카메라 이동크기는 임의로 잡아봄 몇이누?
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        mapUtil.setGoogleMap(p0)
        p0?.setMinZoomPreference(MIN_ZOOM)  // set zoom level bounds
        p0?.setMaxZoomPreference(MAX_ZOOM)
        
        // apply tile overlay to google map
        setTileMaskTileMaker(PositionsMaskTileMaker(
            MapSectionMaskTileMaker(
                MapSectionManager(
                    listOf()
                )
            )
        ))
    }
}