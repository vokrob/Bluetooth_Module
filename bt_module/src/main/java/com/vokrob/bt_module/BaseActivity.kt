package com.vokrob.bt_module

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

    private fun initRcView() {
        val rcView = findViewById<RecyclerView>(R.id.rcViewPaired)
        rcView.layoutManager = LinearLayoutManager(this)

        val adapter = ItemAdapter()
        rcView.adapter = adapter

        adapter.submitList(createDeviceList())
    }

    private fun createDeviceList(): List<ListItem> {
        val list = ArrayList<ListItem>()

        for (i in 0 until 5) {
            list.add(
                ListItem(
                    "Device $i",
                    "34:56:89:56"
                )
            )
        }
        return list
    }
}

























