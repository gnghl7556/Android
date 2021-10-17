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
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;

    private final static int REQUEST_ENABLE_BT = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
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
        if (!btAdapter.isEnabled()) { // 이 스마트폰에서 블루투스사용 기능이 OFF상태라면
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // 블루투스 기능을 ON시킨다.
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        final String address = getIntent().getStringExtra("bluetooth_address");
        boolean flag = true;
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        connectSelectedDevice(device,address);


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
        btHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    textView2 = findViewById(R.id.textView2);
                    textView2.setText(readMessage);
                }
            }
        };
    }



    public void onClickButtonCall() {

    }

    public void onClickButtonLock() {
    }

    public void onClickButtonBackSensor() {
    }

    public void onClickButtonLed() {
        if (Led_bool != true ) {
            connectedThread.write("1");
            Led_bool = true;
        } else {
            connectedThread.write("2");
            Led_bool = false;
        }
    }

    public void onClickButtonCheckSpeed() {
    }

    public void onClickButtonBackPage() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    void connectSelectedDevice(BluetoothDevice device, String selectedDeviceAddress){
        try{
            blueSocket = device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
            blueSocket.connect();
            connectedThread = new ConnectedThread(blueSocket);
            connectedThread.start();
            btHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
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

