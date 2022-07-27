package com.top1shvetsvadim1.gallery.presentation.activity

import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
        //TODO: ctrl + alt + L
        if(permissionGranted){
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission(){
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
        //TODO: ctrl + alt + L
        if(requestCode == READ_EXTERNAL_STORAGE_RC && grantResults.isNotEmpty()){
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if(permissionGranted){
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            } else {
                //TODO: you are using slightly deprecated alert dialog. You can try MaterialDialogBuilder
                AlertDialog.Builder(this).apply {
                    //TODO: hardcoded strings is prohibited, while you are working at android project.
                    //TODO: extract strings in resource file
                    setTitle("Разрешение")
                    setMessage("Для работы приложения необходимо дать разрешение на изспользование медиафайлов")
                    setPositiveButton("ОК"
                    ) { dialog, which ->
                        dialog.dismiss()
                        requestPermission()
                    }
                    setNegativeButton("Нет"
                    ) { dialog, which ->
                        dialog.dismiss()
                    }
                }.show()
                Log.d("MainActivityTest", "Не было данно разрешение")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object{
        private const val READ_EXTERNAL_STORAGE_RC = 228
    }

}