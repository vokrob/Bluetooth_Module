package com.vokrob.bt_module

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vokrob.bt_module.databinding.FragmentListBinding

class DeviceListFragment : androidx.fragment.app.Fragment(), ItemAdapter.Listener {
    private var preferences: SharedPreferences? = null
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var discoveryAdapter: ItemAdapter
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

        binding.imBluetoothSearch.setOnClickListener {
            try {
                bAdapter?.startDiscovery()
            } catch (e: SecurityException) {
            }
        }

        intentFilters()
        checkPermissions()
        initRcViews()
        registerBtLauncher()
        initBtAdapter()
        bluetoothState()
    }

    private fun initRcViews() = with(binding) {
        rcViewPaired.layoutManager = LinearLayoutManager(requireContext())
        rcViewSearch.layoutManager = LinearLayoutManager(requireContext())

        itemAdapter = ItemAdapter(this@DeviceListFragment, false)
        discoveryAdapter = ItemAdapter(this@DeviceListFragment, true)

        rcViewPaired.adapter = itemAdapter
        rcViewSearch.adapter = discoveryAdapter
    }

    private fun getPairedDevices() {
        try {
            val list = ArrayList<ListItem>()
            val deviceList = bAdapter?.bondedDevices as Set<BluetoothDevice>

            deviceList.forEach {
                list.add(
                    ListItem(
                        it,
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

    override fun onClick(item: ListItem) {
        saveMac(item.device.address)
    }

    private val bReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val list = mutableSetOf<ListItem>()
                list.addAll(discoveryAdapter.currentList)
                if (device != null) list.add(ListItem(device, false))
                discoveryAdapter.submitList(list.toList())
                binding.tvEmptySearch.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE

                try {
                    Log.d("MyLog", "Device: ${device?.name}")
                } catch (e: SecurityException) {
                }
            } else if (intent?.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                getPairedDevices()
            } else if (intent?.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
            }
        }
    }

    private fun intentFilters() {
        val f1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val f2 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        val f3 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

        activity?.registerReceiver(bReceiver, f1)
        activity?.registerReceiver(bReceiver, f2)
        activity?.registerReceiver(bReceiver, f3)
    }
}





















