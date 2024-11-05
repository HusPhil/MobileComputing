package com.husph.mobilecomputing.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
    private DeviceAdapter deviceListAdapter;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    BluetoothDevice[] pairedDevices;

    Switch sw_toggleBluetooth;
    TextView tv_bluetoothStatus;
    Button btn_receiveFile;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;


    int REQUEST_ENABLE_BLUETOOTH=1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");


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

    private void InitializeComponents() {


        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        sw_toggleBluetooth = findViewById(R.id.sw_toggleBluetooth);

        tv_bluetoothStatus = findViewById(R.id.tv_bluetoothStatus);

        btn_receiveFile = findViewById(R.id.btn_receiveFile);
        btn_receiveFile.setOnClickListener(v -> {
            btn_receiveFile_OnClickListener();
        });


        Button btn_discoverDevices = findViewById(R.id.btn_discoverDevices);
        btn_discoverDevices.setOnClickListener(v -> {
            btn_discoverDevices_OnClickListener();
        });

        Button btn_sendFile = findViewById(R.id.btn_sendFile);

        ListView listDevices = findViewById(R.id.lv_pairedDevices);

        deviceListAdapter = new DeviceAdapter(this, new ArrayList<>());
        deviceListAdapter.add("Header:Discover devices");
        listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listDevices_OnItemClickListener(i);
            }
        });

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

        btn_discoverDevices_OnClickListener();


    }

    private void listDevices_OnItemClickListener(int i) {
        // Check if the clicked item is a header
        int adjustedIndex = 0;
        int pairedDeviceListIndexStart = -1;

        for(int j = 0; i < deviceListAdapter.getCount(); j++) {

            if(deviceListAdapter.getItem(j).contains("Header:Paired devices")) {
                if(i <= j) break;
                pairedDeviceListIndexStart = j;
                break;
            }
        }

        if(pairedDeviceListIndexStart == -1) {
            if (!deviceListAdapter.getItem(i).contains("Header")) {
                Toast.makeText(this, "Unpaired device", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        adjustedIndex = i - pairedDeviceListIndexStart - 1;

//         Retrieve the actual BluetoothDevice
        BluetoothDevice selectedDevice = pairedDevices[adjustedIndex];
        Toast.makeText(this, "Clicked: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
    }

    private void btn_receiveFile_OnClickListener() {
        ServerClass serverClass = new ServerClass();
        serverClass.start();
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
                            deviceListAdapter.insert(deviceInfo, 1);
                            deviceListAdapter.notifyDataSetChanged();
                        });
                    } catch (SecurityException e) {
                        Log.e(TAG, "Security Exception: " + e.getMessage());
                    }
                }
            }
        }
    };

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

        ArrayList<String> deviceList = new ArrayList<>();
        deviceList.add("Header:Discovered devices");
        deviceList.add("Header:Paired devices");

        int index = 0;
        Set<BluetoothDevice> retrievedDevices = bluetoothAdapter.getBondedDevices();
        pairedDevices = new BluetoothDevice[retrievedDevices.size()];

        if(!retrievedDevices.isEmpty()) {
            for(BluetoothDevice device : retrievedDevices) {
                pairedDevices[index] = device;
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                deviceList.add(deviceName + " :: " + deviceHardwareAddress);
                index++;
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

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            switch (message.what) {
                case STATE_LISTENING:
                    tv_bluetoothStatus.setText("Status: Listening");
                    break;
                case STATE_CONNECTING:
                    tv_bluetoothStatus.setText("Status: Connecting");
                    break;

                case STATE_CONNECTED:
                    tv_bluetoothStatus.setText("Status: Connected");
                    break;

                case STATE_CONNECTION_FAILED:
                    tv_bluetoothStatus.setText("Status: Connection Failed");
                    break;

                case STATE_MESSAGE_RECEIVED:
                    //later
                    break;
            }

            return false;
        }
    });

    class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                    return;
                }
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            }
            catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while(socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_LISTENING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                }
                catch (IOException e) {
                    Log.e(TAG, "Socket's listen() method failed", e);
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    break;
                }
            }

        }
    }

    class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass() {
            try {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                    return;
                }
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    return;
                }
                socket.connect();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }

}