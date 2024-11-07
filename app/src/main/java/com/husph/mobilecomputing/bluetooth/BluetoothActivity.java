package com.husph.mobilecomputing.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.husph.mobilecomputing.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothActivity";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SELECT_FILE_URI = 2;
    private static final int REQUEST_PERMISSION_CODE = 3 ;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] pairedDevices;
    DeviceAdapter deviceListAdapter;
    SendReceive sendReceive;
    ActivityResultLauncher<String> requestPermissionLauncher;

    Switch sw_toggleBluetooth;
    TextView tv_bluetoothStatus;
    TextView tv_fileName;
    Button btn_receiveFile;
    Button btn_sendFile;
    EditText et_btMessage;
    TextView tv_aboutBluetooth;
    ProgressBar pb_transferProgress;
    byte[] fileAsBytes;

    static final int STATE_DISCONNECTED = 0;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

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
        tv_aboutBluetooth = findViewById(R.id.tv_aboutBluetooth);
        tv_aboutBluetooth.setOnClickListener(v-> {
            Intent openBluetoothActivity = new Intent(this, BluetoothStudyActivity.class);
            startActivity(openBluetoothActivity);
        });

        btn_receiveFile = findViewById(R.id.btn_receiveFile);
        btn_receiveFile.setOnClickListener(v -> {
            btn_receiveFile_OnClickListener();
        });

        pb_transferProgress = findViewById(R.id.pb_transferProgress);
        tv_fileName = findViewById(R.id.tv_fileName);


        Button btn_discoverDevices = findViewById(R.id.btn_discoverDevices);
        btn_discoverDevices.setOnClickListener(v -> {
            btn_discoverDevices_OnClickListener();
        });

        btn_sendFile = findViewById(R.id.btn_sendFile);
        btn_sendFile.setOnClickListener(v -> {
            btn_sendFile_OnClickListener();
        });

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

    private void btn_sendFile_OnClickListener() {

        if (
        ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            Toast.makeText(this, "Permission needed", Toast.LENGTH_SHORT).show();
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            return;
        }
        else if ((ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
            }, REQUEST_PERMISSION_CODE);
            return;
        }

        // If we're not connected, show error
        if (sendReceive == null) {
            Toast.makeText(this, "Not connected to a device", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent openFileDialog = new Intent();
        openFileDialog.setAction(Intent.ACTION_GET_CONTENT);
        openFileDialog.setType("*/*");
        startActivityForResult(openFileDialog, SELECT_FILE_URI);

    }

    public byte[] getFileBytesFromUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (InputStream inputStream = contentResolver.openInputStream(uri)) {
            if (inputStream == null) {
                return null;  // Handle case where inputStream couldn't be opened
            }

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (scheme.equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    // Try to get the display name from the content provider
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (fileName == null) {
            // If we couldn't get the name from the content provider, try the last path segment
            fileName = uri.getLastPathSegment();
        }

        return fileName != null ? fileName : "unknown_file";
    }


    public void writeBytesToFile(byte[] data, String fileName) {
        // Define the directory where you want to save the file
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyAppFiles");

        // Ensure the directory exists
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Create the file object with the given filename
        File file = new File(storageDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
            System.out.println("File written successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        BluetoothDevice selectedDevice = pairedDevices[adjustedIndex];

        // Create new client connection for each transfer
        ClientClass clientClass = new ClientClass(selectedDevice);
        clientClass.start();

        tv_bluetoothStatus.setText("Status: Connecting");

    }

    private void btn_receiveFile_OnClickListener() {
        // Create new server connection for each transfer
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE_URI && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                // Use AsyncTask to convert and send the file asynchronously
                new ConvertAndSendFileTask(this).execute(fileUri);
            }
        }
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
            tv_bluetoothStatus.setText("Status: Disconnected");
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
                    FileTransferData transferData = (FileTransferData) message.obj;
                    int fileSize = message.arg1;
                    int progress = (int) (((float) transferData.data.length / fileSize) * 100);

                    if(progress == 100) {
//                        tv_progress.setVisibility(View.GONE);
                        tv_fileName.setText("Transfer Complete - " + transferData.filename);
                        writeBytesToFile(transferData.data, transferData.filename);
                    } else {
//                        tv_progress.setVisibility(View.VISIBLE);
                        tv_fileName.setText(transferData.filename);
                        pb_transferProgress.setProgress(progress);
                    }
//                    tv_progress.setText(progress + "%");

                    break;
            }

            return false;
        }
    });

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice passedDevice) {
            device = passedDevice;
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
            if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN);
                return;
            }

            if(bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            try {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    return;
                }
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                // Create new SendReceive instance for this connection
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            }
            catch(IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }

    class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private long totalExpectedBytes = -1;
        private int totalBytesReceived = 0;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;

            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            try {
                tmpInputStream = bluetoothSocket.getInputStream();
                tmpOutputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            inputStream = tmpInputStream;
            outputStream = tmpOutputStream;
        }

        public void setTotalExpectedBytes(long totalExpectedBytes) {
            this.totalExpectedBytes = totalExpectedBytes;
        }

        public void run() {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;

            try {
                // First read the filename length (4 bytes)
                byte[] filenameLengthBuffer = new byte[4];
                bytes = inputStream.read(filenameLengthBuffer);
                if (bytes != 4) throw new IOException("Failed to read filename length");
                int filenameLength = ByteBuffer.wrap(filenameLengthBuffer).getInt();

                // Then read the filename
                byte[] filenameBuffer = new byte[filenameLength];
                bytes = inputStream.read(filenameBuffer);
                if (bytes != filenameLength) throw new IOException("Failed to read filename");
                String filename = new String(filenameBuffer, StandardCharsets.UTF_8);

                // Read file size as before
                byte[] sizeBuffer = new byte[8];
                bytes = inputStream.read(sizeBuffer);
                if (bytes == 8) {
                    totalExpectedBytes = ByteBuffer.wrap(sizeBuffer).getLong();
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, (int) totalExpectedBytes, -1,
                            new FileTransferData(filename, (int) totalExpectedBytes, new byte[0])).sendToTarget();
                } else {
                    throw new IOException("Failed to read file size.");
                }

                // Rest of file reading remains the same
                while (true) {
                    bytes = inputStream.read(buffer);
                    if (bytes == -1) break;
                    byteArrayOutputStream.write(buffer, 0, bytes);

                    byte[] fileBytes = byteArrayOutputStream.toByteArray();
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, (int) totalExpectedBytes, -1,
                            new FileTransferData(filename, (int) totalExpectedBytes, fileBytes)).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    class ConvertAndSendFileTask extends AsyncTask<Uri, FileProgress, Boolean> {
        private final Context context;
        private String fileName;

        public ConvertAndSendFileTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_transferProgress.setProgress(0);
        }

        @Override
        protected Boolean doInBackground(Uri... uris) {
            Uri fileUri = uris[0];
            byte[] fileBytes = getFileBytesFromUri(context, fileUri);
            fileName = getFileNameFromUri(context, fileUri);

            if (fileBytes != null && sendReceive != null) {
                try {
                    // Send filename length first
                    byte[] filenameBytes = fileName.getBytes(StandardCharsets.UTF_8);
                    byte[] filenameLengthBytes = ByteBuffer.allocate(4).putInt(filenameBytes.length).array();
                    sendReceive.write(filenameLengthBytes);

                    // Send filename
                    sendReceive.write(filenameBytes);

                    // Send file size
                    byte[] sizeBytes = ByteBuffer.allocate(8).putLong(fileBytes.length).array();
                    sendReceive.write(sizeBytes);

                    // Send file in chunks
                    int totalBytes = fileBytes.length;
                    int bytesSent = 0;
                    int chunkSize = 1024;

                    while (bytesSent < totalBytes) {
                        int remainingBytes = totalBytes - bytesSent;
                        int currentChunkSize = Math.min(chunkSize, remainingBytes);

                        byte[] chunk = new byte[currentChunkSize];
                        System.arraycopy(fileBytes, bytesSent, chunk, 0, currentChunkSize);

                        sendReceive.write(chunk);
                        bytesSent += currentChunkSize;

                        // Calculate progress and publish
                        int progress = (int) ((bytesSent / (float) totalBytes) * 100);
                        publishProgress(new FileProgress(fileName, progress));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(FileProgress... values) {
            if (values.length > 0) {
                FileProgress progress = values[0];
                tv_fileName.setText(progress.fileName);
                pb_transferProgress.setProgress(progress.progress);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                tv_fileName.setText("Transfer Complete - " + fileName);
                Toast.makeText(context, "File sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                tv_fileName.setText("Transfer Failed");
                Toast.makeText(context, "Failed to send file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Helper class to hold progress information
    private static class FileProgress {
        String fileName;
        int progress;

        FileProgress(String fileName, int progress) {
            this.fileName = fileName;
            this.progress = progress;
        }
    }

    // Add this class to hold both filename and data
    private static class FileTransferData {
        String filename;
        int totalExpectedBytes;
        byte[] data;

        FileTransferData(String filename, int totalExpectedBytes, byte[] data) {
            this.filename = filename;
            this.totalExpectedBytes = totalExpectedBytes;
            this.data = data;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}