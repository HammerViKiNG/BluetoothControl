package ru.hammerviking.bluetoothcontrol;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class BluetoothHandler {

    public static final int REQUEST_ENABLE_BT = 1001;
    public static final int REQUEST_PERMISSIONS_BT = 1002;

    private Activity parent;
    private BluetoothAdapter btAdapter;
    private BroadcastReceiver receiver;

    public BluetoothHandler(Activity parent, BroadcastReceiver receiver) {
        this.parent = parent;
        this.receiver = receiver;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBT();
    }

    public boolean isNull() {
        return btAdapter == null;
    }

    public boolean isEnabled() {
        return btAdapter.isEnabled();
    }

    public boolean permissionsGranted() {
        return checkBTPermissions() == 0;
    }

    public int getStatus() {
        if (isNull()) return 1;
        if (!isEnabled()) return 2;
        if (!permissionsGranted()) return 3;
        return 0;
    }

    public void bond(BluetoothDevice device) {
        if (btAdapter.isDiscovering())
            btAdapter.cancelDiscovery();
        device.createBond();
    }

    private void enableBT() {
        if (!isNull()) {
            if (!isEnabled())
                parent.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                        REQUEST_ENABLE_BT);
            if (!permissionsGranted())
                setBTPermissions();
        }
    }

    private int checkBTPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) ? parent.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                + parent.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                + parent.checkSelfPermission(Manifest.permission.BLUETOOTH)
                + parent.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN)
                : 0;
    }

    private void setBTPermissions() {
        parent.requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN },
                REQUEST_PERMISSIONS_BT);
    }

    public int startDiscorvery() { // TODO: repair the logic
        enableBT();
        if (getStatus() != 0) {
            return getStatus();
        }
        else {
            if (btAdapter.isDiscovering())
                btAdapter.cancelDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            parent.registerReceiver(receiver, filter);
            checkBTPermissions();
            btAdapter.startDiscovery();
            return 0;
        }
    }
}