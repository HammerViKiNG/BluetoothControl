package ru.hammerviking.bluetoothcontrol;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BluetoothDevicesAdapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothDevicesAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = getItem(position);
        String deviceName = device.getName();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, null);
        }
        ((TextView) convertView.findViewById(android.R.id.text1))
                .setText( (deviceName == null) ? "Undefined" : deviceName);
        ((TextView) convertView.findViewById(android.R.id.text2))
                .setText(device.getAddress());
        return convertView;
    }
}