package com.example.bike_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    BluetoothSocket btSocket = null;
    BluetoothSocket blueSocket;
    ConnectedThread connectedThread;
    Handler btHandler;


    boolean Led_bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btAdapter = BluetoothAdapter.getDefaultAdapter(); //이 앱을 설치한 스마트폰이 블루투스를 지원하지 않는다면 getDefaultAdater()함수를 사용 시, null값을 출력한다.


        final String address = getIntent().getStringExtra("bluetooth_address");

        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        boolean flag = true;

        try {
            btSocket = createBluetoothSocket(device);
            btSocket.connect();
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        if(flag){
            connectedThread = new ConnectedThread(btSocket);
            connectedThread.start();
        }

        // Get permission

        // Enable bluetooth
        //variables
        btnBackPage = (Button) findViewById(R.id.btnBackPage);
        btnCheckSpeed = (ImageView) findViewById(R.id.btnCheckSpeed);
        btnLed = (ImageView) findViewById(R.id.btnLed);
        btnBackSensor = (ImageView) findViewById(R.id.btnBackSensor);
        btnLock = (ImageView) findViewById(R.id.btnLock);
        btnCall = (ImageView) findViewById(R.id.btnCall);

        btnBackPage.setOnClickListener(view ->{onClickButtonBackPage();});
        btnCheckSpeed.setOnClickListener(view -> {onClickButtonCheckSpeed();});
        btnLed.setOnClickListener(view -> {onClickButtonLed();});
        btnBackSensor.setOnClickListener(view -> {onClickButtonBackSensor();});
        btnLock.setOnClickListener(view -> {onClickButtonLock();});
        btnCall.setOnClickListener(view -> {onClickButtonCall();});
    };




    public void onClickButtonCall() {
        // 내가 설정한 연락처에게 메세지나 전화알림
        // 스마트폰의 가속도 센서를 불러와서 충격감지 이후 알림가게함
    }

    public void onClickButtonLock() {
    }

    public void onClickButtonBackSensor() {
    }

    public void onClickButtonLed() {
        if (Led_bool != true ) {
            connectedThread.write("1");
            connectedThread.write("3");
            connectedThread.write("5");
            Led_bool = true;
            btnLed = findViewById(R.id.btnLed);

        } else {
            connectedThread.write("2");
            connectedThread.write("4");
            connectedThread.write("6");
            Led_bool = false;
        }
    }

    public void onClickButtonCheckSpeed() {
    }

    public void onClickButtonBackPage() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
        private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}

