package com.vokrob.bt_module

import android.bluetooth.BluetoothDevice

data class ListItem(
    val device: BluetoothDevice,
    val isChecked: Boolean
)
