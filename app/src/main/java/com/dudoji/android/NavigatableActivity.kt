package com.dudoji.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.network.utils.NetworkMonitor
import com.dudoji.android.network.activity.NoNetworkActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class NavigatableActivity : AppCompatActivity() {

    // 메뉴 아이템 ID와 대상 Activity 매핑 (대상이 null이면 현재 화면 유지)
    protected abstract val navigationItems: Map<Int, Class<out AppCompatActivity>?>

    //네트워크 모니터 변수 생성
    protected lateinit var networkMonitor: NetworkMonitor

    // 현재 Activity에 해당하는 기본 선택 아이템 ID
    protected abstract val defaultSelectedItemId: Int

    // BottomNavigation 설정 공통 함수
    protected fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.selectedItemId = defaultSelectedItemId
        bottomNav.setOnItemSelectedListener { item ->
            navigationItems[item.itemId]?.let { destination ->
                // 동일 Activity가 이미 최상위에 있는 경우 재생성 방지
                Intent(this, destination).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }.also { startActivity(it) }
            }
            true // 항상 이벤트 소비
        }
    }

    override  fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        networkMonitor = NetworkMonitor(this)
        lifecycle.addObserver(networkMonitor)
    }

    override fun onResume() {
        super.onResume()
        updateBottomNavigationSelection()
        setupNetworkMonitoring()
    }

    private fun updateBottomNavigationSelection() {
        findViewById<BottomNavigationView>(R.id.navigationView)?.apply {
            selectedItemId = defaultSelectedItemId
        }
    }

    protected open fun setupNetworkMonitoring(){
        networkMonitor.onNetworkAvailable = {
            //네트워크가 복구 되었을 때 NoNetworkActivity 끄기
            if (isNoNetworkActivityVisible()){
                finishNoNetworkActivity()
            }
        }

        networkMonitor.onNetworkLost = {
            //네트워크가 끊겼을 때 NoNetworkActivity 실행
            if(!isNoNetworkActivityVisible()){
                startActivity(Intent(this, NoNetworkActivity::class.java))
            }
        }
    }

    //현재 엑티비티가 NoNetworkActivity인지 확인
    private fun isNoNetworkActivityVisible(): Boolean {
        return if(this is NoNetworkActivity) true
        else false
    }

    //현재 엑티비티가 NoNetworkActivity이면 finish
    private fun finishNoNetworkActivity() {
        if(this is NoNetworkActivity){
            finish()
        }
    }
}