package com.dudoji.android.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.dudoji.android.MainActivity
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.map.MapActivity
import com.dudoji.android.repository.LOCATION_SYSTEM_CHANGE_WARNING_TEXT
import com.dudoji.android.repository.MAX_LOG_SIZE
import com.dudoji.android.repository.RevealCircleRepository
import com.dudoji.android.util.NetWorkUtil
import com.dudoji.android.util.PermissionUtil
import com.dudoji.android.util.location.LocationService
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.LinkedList
import java.util.Queue

@Deprecated(LOCATION_SYSTEM_CHANGE_WARNING_TEXT)
class LocationActivity : NavigatableActivity() {

    override val defaultSelectedItemId = R.id.locationFragment

    override val navigationItems = mapOf(
        R.id.homeFragment to MainActivity::class.java,
        R.id.mapFragment to MapActivity::class.java,
        R.id.locationFragment to null
    )

    private lateinit var locationService: LocationService// 로케이션 서비스 변수 추가
    private val locationQueue: Queue<String> = LinkedList()
    private lateinit var locationTextView: TextView
    private lateinit var bottomNav: BottomNavigationView
    //서비스로 옮긴 변수들은 제거

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        initViews()
        initLocationService()
        setLocationTestButton()
    }

    private fun initViews() { // 뷰를 초기화 한다.
        locationTextView = findViewById(R.id.locationTextView)
        bottomNav = findViewById(R.id.navigationView)
        setupBottomNavigation(bottomNav)
    }

    // LocationService 인스턴스 생성 및 권한 체크
    private fun initLocationService() {
        // LocationService 인스턴스 생성
        locationService = LocationService(this)
        // 위치 권한 체크 후 LocationService의 setLocationCallback 호출
        if (PermissionUtil.hasLocationPermission(this)) {
            startLocationUpdatesWithCallback()
        } else {
            PermissionUtil.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)
        }

    }

    //위치 업데이트 함수
    private fun startLocationUpdatesWithCallback() {
        locationService.setLocationCallback(object  : LocationCallback(){//setLocationCallback 호출하여 위치 업데이트 콜백
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.lastLocation?.let { location -> updateLocationLog(location)// 마지막 위치 확인 및 로그 업뎃
                    RevealCircleRepository.addLocation(location)//위치 저장소 업뎃
                }
            }
        })
    }


    private fun setLocationTestButton(){
        val button = findViewById<Button>(R.id.sendButton)
        button.setOnClickListener({
            val jsonObject = NetWorkUtil().createRevealCirclesRequestJson()
            val path = "api/user/reveal_circles/save"
            NetWorkUtil().sendJsonToServer(path, jsonObject)
        })
    }


    private fun showPermissionDeniedToast() {
        Toast.makeText(this, "위치 불러오기 거부", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.handlePermissionResult(
            requestCode, LOCATION_PERMISSION_REQUEST_CODE, grantResults,
            { startLocationUpdatesWithCallback() }, { showPermissionDeniedToast() } //새로운 위치 업뎃 함수
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocationLog(location: Location) {
        val log = "Lat: ${location.latitude}, Lng: ${location.longitude}"
        if (locationQueue.size >= MAX_LOG_SIZE) locationQueue.poll()
        locationQueue.add(log)
        locationTextView.text = locationQueue.joinToString("\n")
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
