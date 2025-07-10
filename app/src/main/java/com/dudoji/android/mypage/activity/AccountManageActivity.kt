package com.dudoji.android.mypage.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dudoji.android.R

class AccountManageActivity : AppCompatActivity() {

    private lateinit var switchCamera: Switch
    private lateinit var switchPhoto: Switch

    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_STORAGE_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_manage)

        switchCamera = findViewById(R.id.switch_camera)
        switchPhoto = findViewById(R.id.switch_photo)

        switchCamera.isChecked = checkCameraPermission()
        switchPhoto.isChecked = checkStoragePermission()

        //카메라 권한 변경 리스너
        switchCamera.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //권한 없으면 요청
                if (!checkCameraPermission()) {
                    switchCamera.isChecked = false // 임시로 다시 OFF
                    requestCameraPermission()
                }
            } else {
                // 이미 권한이 있으면 설정화면으로 안내, 권한 해제는 설정에서
                if (checkCameraPermission()) {
                    showGoToSettingsDialog("카메라")
                }
            }
        }

        // 사진 권한 변경
        switchPhoto.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 권한 없으면 요청
                if (!checkStoragePermission()) {
                    switchPhoto.isChecked = false // 임시로 다시 OFF
                    requestStoragePermission()
                }
            } else {
                // 이미 권한이 있으면 설정화면으로 안내, 권한 해제는 설정에서
                if (checkStoragePermission()) {
                    showGoToSettingsDialog("사진")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        switchCamera.isChecked = checkCameraPermission()
        switchPhoto.isChecked = checkStoragePermission()
    }

    // 카메라 권한 상태 확인
    private fun checkCameraPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 권한 요청
    private fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // 권한 설명 다이얼로그 보여주기
            showPermissionRationaleDialog("카메라", permission, REQUEST_CAMERA_PERMISSION)
        } else {
            if (!checkCameraPermission()) {
                // 다시 묻지 않음 상태면 바로 설정으로 안내
                showGoToSettingsDialog("카메라")
            } else {
                // 권한 실제로 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }
    }

    // 사진 권한 상태 확인
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 사진 권한 요청
    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // 권한 설명 다이얼로그 보여주기
            showPermissionRationaleDialog("사진", permission, REQUEST_STORAGE_PERMISSION)
        } else {
            if (!checkStoragePermission()) {
                // 다시 묻지 않음 상태면 설정화면으로
                showGoToSettingsDialog("사진")
            } else {
                // 권한 실제로 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    REQUEST_STORAGE_PERMISSION
                )
            }
        }
    }

    // 권한 설명창
    private fun showPermissionRationaleDialog(
        permissionName: String,
        permission: String,
        requestCode: Int
    ) {
        AlertDialog.Builder(this)
            .setTitle("$permissionName 권한 필요")
            .setMessage("이 기능을 사용하려면 $permissionName 권한이 필요합니다.")
            .setPositiveButton("권한 요청") { _, _ ->
                // 사용자에게 실제 권한 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
                )
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "카메라 권한 허용됨", Toast.LENGTH_SHORT).show()
                    switchCamera.isChecked = true
                } else {
                    Toast.makeText(this, "카메라 권한 거부됨", Toast.LENGTH_SHORT).show()
                    switchCamera.isChecked = false
                }
            }
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "사진 권한 허용됨", Toast.LENGTH_SHORT).show()
                    switchPhoto.isChecked = true
                } else {
                    Toast.makeText(this, "사진 권한 거부됨", Toast.LENGTH_SHORT).show()
                    switchPhoto.isChecked = false
                }
            }
        }
    }

    // 사진은 허용 안함 누르면 설정에서 다시 권한 허용해야 되서, 거부 상태일 때 직접 변경함
    private fun showGoToSettingsDialog(permissionName: String) {
        AlertDialog.Builder(this)
            .setTitle("$permissionName 권한 설정")
            .setMessage("$permissionName 권한은 '앱 설정 > 권한'에서 직접 변경할 수 있습니다.\n설정 화면으로 이동하시겠습니까?")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("취소") { _, _ ->
                // 취소 시에는 그대로 권한 가지고 있음
                if (permissionName == "카메라") {
                    switchCamera.isChecked = checkCameraPermission()
                } else if (permissionName == "사진") {
                    switchPhoto.isChecked = checkStoragePermission()
                }
            }
            .show()
    }
}
