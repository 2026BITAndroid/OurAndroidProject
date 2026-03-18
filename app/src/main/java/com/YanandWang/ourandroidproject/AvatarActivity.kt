package com.YanandWang.ourandroidproject

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 此页面用来实现选择头像功能
 */
class AvatarActivity : AppCompatActivity() {
    private lateinit var civAvatar: CircleImageView
    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
            } else {
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            }
            if (isGranted) {
                openGallery()
            }
        }
    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->
            imageUri?.let {
                civAvatar.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)
        civAvatar = findViewById(R.id.civAvatar)
        val btnSelectAvatar = findViewById<Button>(R.id.btnSelectAvatar)

        btnSelectAvatar.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }
    }
    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun requestPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions)
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }
}