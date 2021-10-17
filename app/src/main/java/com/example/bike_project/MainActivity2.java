package com.example.bike_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {
    String TAG = "MainActivity2";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    Button btnBackPage;
    ImageView btnCheckSpeed ,btnLed, btnBackSensor, btnLock, btnCall;
    TextView textView2,textView3,textView4,textView5,textView6;

    BluetoothAdapter btAdapter;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;

    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;


    int Led_bool = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        String name = getIntent().getStringExtra("bluetooth_address");


        // Get permission

        // Enable bluetooth
        //variables
        btnBackPage = (Button) findViewById(R.id.btnBackPage);
        btnCheckSpeed = (ImageView) findViewById(R.id.btnCheckSpeed);
        btnLed = (ImageView) findViewById(R.id.btnLed);
        btnBackSensor = (ImageView) findViewById(R.id.btnBackSensor);
        btnLock = (ImageView) findViewById(R.id.btnLock);
        btnCall = (ImageView) findViewById(R.id.btnCall);

        btnBackPage.setOnClickListener(view ->{
            onClickButtonBackPage();
        });

        btnCheckSpeed.setOnClickListener(view -> {
            onClickButtonCheckSpeed();
        });

        btnLed.setOnClickListener(view -> {
            onClickButtonLed();
        });

        btnBackSensor.setOnClickListener(view -> {
            onClickButtonBackSensor();
        });

        btnLock.setOnClickListener(view -> {
            onClickButtonLock();
        });

        btnCall.setOnClickListener(view -> {
            onClickButtonCall();
        });
    }

    public void onClickButtonCall() {
    }

    public void onClickButtonLock() {
    }

    public void onClickButtonBackSensor() {
    }

    public void onClickButtonLed() {
        if (Led_bool != 1 ) {
            connectedThread.write("1");
            Led_bool = 1;
        } else {
            connectedThread.write("2");
            Led_bool = 0;
        }
    }

    public void onClickButtonCheckSpeed() {
    }

    public void onClickButtonBackPage() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}

