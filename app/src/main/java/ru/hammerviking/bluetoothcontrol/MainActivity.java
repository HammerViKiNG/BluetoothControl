package ru.hammerviking.bluetoothcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnBt;
    private TextView textView;
    private ListView btDevicesListView;
    private BluetoothHandler btHandler;
    private BluetoothDevice device;

    private BluetoothDevicesAdapter btDevicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btHandler = new BluetoothHandler(this, btReceiver);
        if (btHandler.isNull()) {
            Toast.makeText(this, "Your device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        btDevicesList = new BluetoothDevicesAdapter(this, android.R.layout.simple_list_item_2);

        initializeLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(btReceiver);
    }

    private void initializeLayout() {
        btnBt = findViewById(R.id.btnBt);
        textView = findViewById(R.id.textView);
        btDevicesListView = findViewById(R.id.btDevicesList);

        btnBt.setOnClickListener(this);
        btDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                device = (BluetoothDevice)btDevicesListView.getItemAtPosition(position);
                btHandler.bond(device);
                Toast.makeText(MainActivity.this, "Bond complete!", Toast.LENGTH_LONG).show();
                BluetoothClient client = new BluetoothClient(device);
                client.run();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            switch (requestCode) {
                case BluetoothHandler.REQUEST_ENABLE_BT : {
                    Toast.makeText(this, "Please, turn on Bluetooth", Toast.LENGTH_LONG).show();
                    break;
                }
                case BluetoothHandler.REQUEST_PERMISSIONS_BT : {
                    Toast.makeText(this, "Please, grant the permissions", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        btDevicesList.clear();
        btDevicesListView.setAdapter(btDevicesList);
        btHandler.startDiscorvery();
    }

    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_FOUND: {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    btDevicesList.add(device);
                    btDevicesListView.setAdapter(btDevicesList);
                    break;
                }
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED: {
                    Toast.makeText(MainActivity.this, "Discovery started...", Toast.LENGTH_SHORT).show();
                    break;
                }
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED: {
                    Toast.makeText(MainActivity.this, "Discovery finished", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };
}