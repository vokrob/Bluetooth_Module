package com.vokrob.bt_module.bluetooth

import android.bluetooth.BluetoothAdapter

class BluetoothController(private val adapter: BluetoothAdapter) {
    private var connectThread: ConnectThread? = null

    fun connect(mac: String, listener: Listener) {
        if (adapter.isEnabled && mac.isNotEmpty()) {
            val device = adapter.getRemoteDevice(mac)
            connectThread = ConnectThread(device, listener)
            connectThread?.start()
        }
    }

    companion object {
        const val BLUETOOTH_CONNECTED = "bluetooth_connected"
        const val BLUETOOTH_NO_CONNECTED = "bluetooth_no_connected"
    }

    interface Listener {
        fun onReceive(message: String)
    }
}
























