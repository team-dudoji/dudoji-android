package com.dudoji.android.login.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class RequestPermissionsUtil(private val activity: Activity) {

    companion object {
        //위치, 사진, 카메라 한번에 <- 로그인 액티비티
        const val REQUEST_INITIAL_PERMISSIONS = 1
        //위치, 사진 따로 <- 마이어카운트 액티비티
        const val REQUEST_CAMERA_PERMISSION = 100
        const val REQUEST_IMAGE_PERMISSION = 101
    }

    // 초기값 3인방 권한 요청 <- 로그인 액티비티
    fun requestInitialPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // 위치 권한 확인 (버전에 따라 다르게 처리)
        val locationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        locationPermissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        // 카메라 권한 확인
        if (!isCameraPermissionGranted()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        // 사진/저장소 권한 확인
        if (!isImagePermissionGranted()) {
            val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            permissionsToRequest.add(imagePermission)
        }

        // 요청할 권한이 있는 경우에만 사용자에게 요청
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toTypedArray(),
                REQUEST_INITIAL_PERMISSIONS
            )
        }
    }

    //카메라 권한이 이미 허용되었는지 확인
    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    //이미지 권한이 이미 허용되었는지 확인
    fun isImagePermissionGranted(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    //사용자에게 카메라 권한을 요청하는 팝업을 띄움
    fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            showPermissionRationaleDialog("카메라", permission, REQUEST_CAMERA_PERMISSION)
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CAMERA_PERMISSION)
        }
    }

    //사용자에게 이미지 권한을 요청하는 팝업을 띄움
    fun requestImagePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            showPermissionRationaleDialog("사진", permission, REQUEST_IMAGE_PERMISSION)
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_IMAGE_PERMISSION)
        }
    }
    //설정창 이동하는 다이얼로그
    fun showGoToSettingsDialog(title: String, message: String, onCancel: () -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle(title) // 전달받은 title 사용
            .setMessage(message) // 전달받은 message 사용
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                activity.startActivity(intent)
            }
            .setNegativeButton("취소") { _, _ -> onCancel() }
            .setOnCancelListener { onCancel() }
            .show()
    }
    //권한 요청 다이얼로그
    private fun showPermissionRationaleDialog(permissionName: String, permission: String, requestCode: Int) {
        AlertDialog.Builder(activity)
            .setTitle("$permissionName 권한 필요")
            .setMessage("이 기능을 사용하려면 $permissionName 권한이 필요합니다.")
            .setPositiveButton("권한 요청") { _, _ ->
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }
            .setNegativeButton("취소", null)
            .show()
    }
}