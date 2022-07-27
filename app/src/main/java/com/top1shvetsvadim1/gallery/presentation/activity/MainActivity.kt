package com.top1shvetsvadim1.gallery.presentation.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.top1shvetsvadim1.gallery.R
import com.top1shvetsvadim1.gallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (permissionGranted) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_RC
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_RC && grantResults.isNotEmpty()) {
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            } else {
                MaterialAlertDialogBuilder(this).apply {
                    setTitle(getString(R.string.text_permission))
                    setMessage(getString(R.string.dialogue_permission_text))
                    setNegativeButton(getString(R.string.button_negative_dialog)) { dialog, which ->
                        dialog.dismiss()
                    }
                    setPositiveButton(getString(R.string.button_positive_dialog)) { dialog, which ->
                        dialog.dismiss()
                        requestPermission()
                    }
                }.show()
                Log.d("MainActivityTest", getString(R.string.log_message_no_permission))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val READ_EXTERNAL_STORAGE_RC = 228
    }

}