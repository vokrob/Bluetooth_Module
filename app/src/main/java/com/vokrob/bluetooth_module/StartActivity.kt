package com.vokrob.bluetooth_module

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.vokrob.bluetooth_module.databinding.ContentStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ContentStartBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(com.vokrob.bt_module.R.color.black_dark, null)
        window.decorView.systemUiVisibility = 0
    }
}






















