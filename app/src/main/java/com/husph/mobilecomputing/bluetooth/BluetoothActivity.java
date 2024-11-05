package com.husph.mobilecomputing.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.husph.mobilecomputing.MainActivity;
import com.husph.mobilecomputing.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "BluetoothActivity";
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> deviceListAdapter;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    Set<BluetoothDevice> pairedDevices;
    String discoveredDevice;

    Switch sw_toggleBluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bluetooth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeComponents();


    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(context, "Discovery Started", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "Discovery Finished", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    try {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        String deviceInfo = (deviceName != null ? deviceName : "Unknown") + " :: " + deviceHardwareAddress;
                        runOnUiThread(() -> {
                            deviceListAdapter.insert(deviceInfo, 0);  // Insert at position 0 (top)
                            deviceListAdapter.notifyDataSetChanged();
                        });
                    } catch (SecurityException e) {
                        Log.e(TAG, "Security Exception: " + e.getMessage());
                    }
                }
            }
        }
    };

    private void InitializeComponents() {


        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        sw_toggleBluetooth = findViewById(R.id.sw_toggleBluetooth);

        Button btn_discoverDevices = findViewById(R.id.btn_discoverDevices);
        btn_discoverDevices.setOnClickListener(v -> {
            btn_discoverDevices_OnClickListener();
        });

        Button btn_sendFile = findViewById(R.id.btn_sendFile);

        ListView listDevices = findViewById(R.id.lv_availableDevices);
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listDevices.setAdapter(deviceListAdapter);

        if(bluetoothAdapter.isEnabled()) {
            sw_toggleBluetooth.setChecked(true);
        }

        sw_toggleBluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleBluetooth(isChecked);
        });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission is granted, proceed with Bluetooth tasks
                        Toast.makeText(this, "Bluetooth permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        // Permission is denied, show a message to the user
                        Toast.makeText(this, "Bluetooth permission is required to connect", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }

    private void btn_discoverDevices_OnClickListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Bluetooth permission failed", Toast.LENGTH_SHORT).show();
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN);
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        boolean discover = bluetoothAdapter.startDiscovery();

        Toast.makeText(this, "Scanning: " + discover, Toast.LENGTH_SHORT).show();

        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> deviceList = new ArrayList<>();



//        if (!discoveredDevice.isEmpty()) {
//            deviceList.add(discoveredDevice);
//        }

        if (!pairedDevices.isEmpty()) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                deviceList.add(deviceName + " :: " + deviceHardwareAddress);
            }
        }
        deviceListAdapter.clear();
        deviceListAdapter.addAll(deviceList);
    }

    private void toggleBluetooth(boolean isChecked) {
        if (isChecked) {
            enableBluetooth();
        }
        else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth permission failed", Toast.LENGTH_SHORT).show();
                sw_toggleBluetooth.setChecked(true);
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                return;
            }
            bluetoothAdapter.disable();
            Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth failed", Toast.LENGTH_SHORT).show();
                sw_toggleBluetooth.setChecked(false);
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Bluetooth already enabled", Toast.LENGTH_SHORT).show();
        }

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }

}