package com.vokrob.bt_module

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vokrob.bt_module.databinding.FragmentListBinding

class DeviceListFragment : Fragment(), ItemAdapter.Listener {
    private var preferences: SharedPreferences? = null
    private lateinit var itemAdapter: ItemAdapter
    private var bAdapter: BluetoothAdapter? = null
    private lateinit var binding: FragmentListBinding
    private lateinit var btLauncher: ActivityResultLauncher<Intent>
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences =
            activity?.getSharedPreferences(BluetoothConstants.PREFERENCES, Context.MODE_PRIVATE)
        binding.imBluetoothOn.setOnClickListener {
            btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        checkPermissions()
        initRcViews()
        registerBtLauncher()
        initBtAdapter()
        bluetoothState()
    }

    private fun initRcViews() = with(binding) {
        rcViewPaired.layoutManager = LinearLayoutManager(requireContext())
        itemAdapter = ItemAdapter(this@DeviceListFragment)
        rcViewPaired.adapter = itemAdapter
    }

    private fun getPairedDevices() {
        try {
            val list = ArrayList<ListItem>()
            val deviceList = bAdapter?.bondedDevices as Set<BluetoothDevice>

            deviceList.forEach {
                list.add(
                    ListItem(
                        it.name,
                        it.address,
                        preferences?.getString(BluetoothConstants.MAC, "") == it.address
                    )
                )
            }
            binding.tvEmptyPaired.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            itemAdapter.submitList(list)
        } catch (e: SecurityException) {

        }
    }

    private fun initBtAdapter() {
        val bManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = bManager.adapter
    }

    private fun bluetoothState() {
        if (bAdapter?.isEnabled == true) {
            changeButtonColor(binding.imBluetoothOn, Color.GREEN)
            getPairedDevices()
        }
    }

    private fun registerBtLauncher() {
        btLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                changeButtonColor(binding.imBluetoothOn, Color.GREEN)
                getPairedDevices()
                Snackbar.make(binding.root, R.string.bluetooth_on, Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(binding.root, R.string.bluetooth_off, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun checkPermissions() {
        if (!checkBtPermissions()) {
            registerPermissionListener()
            launchBtPermissions()
        }
    }

    private fun launchBtPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pLauncher.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            pLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun registerPermissionListener() {
        pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            }
    }

    private fun saveMac(mac: String) {
        val editor = preferences?.edit()
        editor?.putString(BluetoothConstants.MAC, mac)
        editor?.apply()
    }

    override fun onClick(device: ListItem) {
        saveMac(device.mac)
    }
}





















