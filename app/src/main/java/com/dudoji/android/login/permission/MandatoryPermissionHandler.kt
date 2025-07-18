package com.dudoji.android.login.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AlertDialog

class MandatoryPermissionHandler(
    private val activity: Activity,
    private val listener: PermissionResultListener
) {
    private val requestPermissionsUtil = RequestPermissionsUtil(activity)

    interface PermissionResultListener {
        fun onAppShouldBeTerminated()
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("PermissionHandler", "requestCode=$requestCode, permissions=${permissions.joinToString()}, grantResults=${grantResults.joinToString()}")

        if (requestCode == RequestPermissionsUtil.REQUEST_INITIAL_PERMISSIONS) {
            val locationPermissionIndex = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)

            // 위치 권한이 거부되었는지 확인
            if (locationPermissionIndex != -1 && grantResults[locationPermissionIndex] == PackageManager.PERMISSION_DENIED) {
                showEssentialPermissionInfoDialog()
            }
        }
    }
    
    private fun showEssentialPermissionInfoDialog() {
        AlertDialog.Builder(activity)
            .setTitle("필수 권한 안내")
            .setMessage("이 앱의 핵심 기능을 사용하려면 '위치' 권한이 반드시 필요합니다. 원활한 서비스 이용을 위해 권한을 허용해주세요.")
            .setPositiveButton("확인") { _, _ ->
                // 설정으로 이동할지 묻는 다이얼로그 호출
                val title = "위치 권한 설정"
                val message = "'앱 설정 > 권한'에서 직접 변경할 수 있습니다.\n설정 화면으로 이동하시겠습니까?"
                requestPermissionsUtil.showGoToSettingsDialog(title, message) {
                    // 사용자가 설정 이동을 취소하면 리스너를 통해 Activity에 알림
                    listener.onAppShouldBeTerminated()
                }
            }
            .setCancelable(false)
            .show()
    }
}
