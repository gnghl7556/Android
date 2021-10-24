package com.example.bike_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class serviceActivity extends AppCompatActivity {
    String TAG = "MainActivity2";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    Button btnBackPage;
    ImageView btnCheckSpeed ,btnLed, btnBackSensor, btnLock, btnCall;
    TextView textView1, textView2,textView3,textView4,textView5,textView6;

    BluetoothAdapter btAdapter;

    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;
    String tempaddres;
    boolean Speed_bool = false;
    boolean Sensor_bool = false;
    boolean Lock_bool = false;
    boolean Led_bool = false;
    boolean Call_bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);

        btAdapter = BluetoothAdapter.getDefaultAdapter(); //이 앱을 설치한 스마트폰이 블루투스를 지원하지 않는다면 getDefaultAdater()함수를 사용 시, null값을 출력한다.



        final String address = getIntent().getStringExtra("bluetooth_address");
        tempaddres = address;
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
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);

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
        if (Call_bool!= true){
            btnCall.setImageResource(R.drawable.dark_warning_on);
            textView1.setTextColor(Color.parseColor("#fa9b36"));
            Call_bool = true;
        }else{
            btnCall.setImageResource(R.drawable.dark_warning_off);
            textView1.setTextColor(Color.parseColor("#ffffff"));
            Call_bool = false;
        }
    }

    public void onClickButtonLock() {
        if (Lock_bool!= true){
            connectedThread.write("7");
            btnLock.setImageResource(R.drawable.dark_lock_on);
            textView4.setTextColor(Color.parseColor("#fa9b36"));
            Lock_bool = true;
        }else{
            connectedThread.write("8");
            btnLock.setImageResource(R.drawable.dark_lock_off);
            textView4.setTextColor(Color.parseColor("#ffffff"));
            Lock_bool = false;
        }
    }

    public void onClickButtonBackSensor() {
        if (Sensor_bool!= true){
            connectedThread.write("3");
            btnBackSensor.setImageResource(R.drawable.dark_sensor_on);
            textView3.setTextColor(Color.parseColor("#fa9b36"));
            Sensor_bool = true;
        }else{
            connectedThread.write("4");
            btnBackSensor.setImageResource(R.drawable.dark_sensor_off);
            textView3.setTextColor(Color.parseColor("#ffffff"));
            Sensor_bool = false;
        }
    }

    public void onClickButtonLed() {
        if (Led_bool != true ) {
            connectedThread.write("1");
            Led_bool = true;
            btnLed.setImageResource(R.drawable.dark_led_on);
            textView6.setTextColor(Color.parseColor("#fa9b36"));


        } else {
            connectedThread.write("2");
            Led_bool = false;
            btnLed.setImageResource(R.drawable.dark_led_off);
            textView6.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    public void onClickButtonCheckSpeed() {
        if (Speed_bool!= true){
            btnCheckSpeed.setImageResource(R.drawable.dark_speed_on);
            textView5.setTextColor(Color.parseColor("#fa9b36"));
            Speed_bool = true;
            Intent intent = new Intent(getApplicationContext(), speedActivity.class);
            intent.putExtra("bluetooth_address",tempaddres);
            connectedThread.cancel(); //쓰레드를 취소
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }else{
            btnCheckSpeed.setImageResource(R.drawable.dark_speed_off);
            textView5.setTextColor(Color.parseColor("#ffffff"));
            Speed_bool = false;
        }
    }

    public void onClickButtonBackPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("서비스 연결을 종료하겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("결제하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connectedThread.cancel(); //쓰레드를 취소
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        builder.create();
        builder.show();

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

