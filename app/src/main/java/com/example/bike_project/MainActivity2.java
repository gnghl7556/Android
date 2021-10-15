package com.example.bike_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {
    String TAG = "MainActivity2";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    Button btnBackPage, btnCheckSpeed ,btnLed, btnBackSensor, btnLock, btnCall;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;

    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Get permission
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity2.this, permission_list, 1);

        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter(); //이 앱을 설치한 스마트폰이 블루투스를 지원하지 않는다면 getDefaultAdater()함수를 사용 시, null값을 출력한다.
        if (!btAdapter.isEnabled()) { // 이 스마트폰에서 블루투스사용 기능이 OFF상태라면
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // 블루투스 기능을 ON시킨다.
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //variables
        btnBackPage = (Button) findViewById(R.id.btnBackPage);
        btnCheckSpeed = (Button) findViewById(R.id.btnCheckSpeed);
        btnLed = (Button) findViewById(R.id.btnLed);
        btnBackSensor = (Button) findViewById(R.id.btnBackSensor);
        btnLock = (Button) findViewById(R.id.btnLock);
        btnCall = (Button) findViewById(R.id.btnCall);

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
    }

    public void onClickButtonCheckSpeed() {
    }

    public void onClickButtonBackPage() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}