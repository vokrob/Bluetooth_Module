package com.vokrob.bt_module

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(com.vokrob.bt_module.R.color.black_dark, null)

        // initRcView()

        supportFragmentManager.beginTransaction().replace(R.id.placeHolder, DeviceListFragment())
            .commit()
    }
}

























