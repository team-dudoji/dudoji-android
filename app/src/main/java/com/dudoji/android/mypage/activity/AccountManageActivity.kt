package com.dudoji.android.mypage.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.dudoji.android.R
import com.dudoji.android.presentation.util.RequestPermissionsUtil
import com.dudoji.android.mypage.repository.MyPageRemoteDataSource
import kotlinx.coroutines.launch

class AccountManageActivity : AppCompatActivity() {

    private lateinit var switchCamera: Switch
    private lateinit var switchPhoto: Switch
    private lateinit var requestPermissionsUtil: RequestPermissionsUtil

    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_STORAGE_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_manage)

        requestPermissionsUtil = RequestPermissionsUtil(this)

        switchCamera = findViewById(R.id.switch_camera)
        switchPhoto = findViewById(R.id.switch_photo)

        val arrowPath = "file:///android_asset/account/ic_arrow_forward.png"
        findViewById<ImageView>(R.id.arrow_change_password).load(arrowPath)
        findViewById<ImageView>(R.id.arrow_two_factor).load(arrowPath)
        findViewById<ImageView>(R.id.arrow_logout).load(arrowPath)
        findViewById<ImageView>(R.id.arrow_withdraw).load(arrowPath)

        loadUserProfile()

        updatePermissionSwitches()

        switchCamera.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!requestPermissionsUtil.isCameraPermissionGranted()) {
                    requestPermissionsUtil.requestCameraPermission()
                }
            } else {
                if (requestPermissionsUtil.isCameraPermissionGranted()) {
                    val title = "카메라 권한 설정"
                    val message = "카메라 권한은 '앱 설정 > 권한'에서 직접 변경할 수 있습니다.\n설정 화면으로 이동하시겠습니까?"
                    requestPermissionsUtil.showGoToSettingsDialog(title, message) {
                        switchCamera.isChecked = true
                    }
                }
            }
        }

        switchPhoto.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!requestPermissionsUtil.isImagePermissionGranted()) {
                    requestPermissionsUtil.requestImagePermission()
                }
            } else {
                if (requestPermissionsUtil.isImagePermissionGranted()) {
                    val title = "사진 권한 설정"
                    val message = "사진 권한은 '앱 설정 > 권한'에서 직접 변경할 수 있습니다.\n설정 화면으로 이동하시겠습니까?"
                    requestPermissionsUtil.showGoToSettingsDialog(title, message) {
                        switchPhoto.isChecked = true
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionSwitches()
    }

    private fun updatePermissionSwitches() {
        switchCamera.isChecked = requestPermissionsUtil.isCameraPermissionGranted()
        switchPhoto.isChecked = requestPermissionsUtil.isImagePermissionGranted()
    }

    private fun loadUserProfile() {
        val tvEmail = findViewById<TextView>(R.id.tv_email)
        val etNickname = findViewById<EditText>(R.id.et_nickname)

        lifecycleScope.launch {
            try {
                val userProfile = MyPageRemoteDataSource.getUserProfile()
                userProfile?.let { profile ->
                    tvEmail.text = profile.email
                    etNickname.setText(profile.name)
                }
            } catch (e: Exception) {
                Toast.makeText(this@AccountManageActivity, "프로필을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        updatePermissionSwitches()

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "카메라 권한 허용됨", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "카메라 권한 거부됨", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "사진 권한 허용됨", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "사진 권한 거부됨", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}