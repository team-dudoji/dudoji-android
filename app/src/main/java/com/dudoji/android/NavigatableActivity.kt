package com.dudoji.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class NavigatableActivity : AppCompatActivity() {

    // 메뉴 아이템 ID와 대상 Activity 매핑 (대상이 null이면 현재 화면 유지)
    protected abstract val navigationItems: Map<Int, Class<out AppCompatActivity>?>

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
}