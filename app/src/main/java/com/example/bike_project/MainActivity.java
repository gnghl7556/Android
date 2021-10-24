package com.example.bike_project;



import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Set;




public class MainActivity extends AppCompatActivity {
    TextView textStatus;
    Button btnSearch, btnNext;
    ImageView btnParied;

    ListView listView;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;
    String temp_name = null;
    String temp_addres = null;
    private final static int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get permission
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity.this, permission_list, 1);

        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter(); //이 앱을 설치한 스마트폰이 블루투스를 지원하지 않는다면 getDefaultAdater()함수를 사용 시, null값을 출력한다.
        if (!btAdapter.isEnabled()) { // 이 스마트폰에서 블루투스사용 기능이 OFF상태라면
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // 블루투스 기능을 ON시킨다.
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Intent intent = new Intent(this, serviceActivity.class);
        // variables
        textStatus = (TextView) findViewById(R.id.text_status);
        btnParied = (ImageView) findViewById(R.id.btn_paired);
        btnSearch = (Button) findViewById(R.id.btn_search);

        listView = (ListView) findViewById(R.id.listview);
        btnNext = (Button) findViewById(R.id.btn_next);

        btnSearch.setOnClickListener(view -> {
            onClickButtonSearch();
        });

        btnParied.setOnClickListener(view -> {
            onClickButtonPaired();
        });

        btnNext.setOnClickListener(view -> {
            onClickButtonNext();
        });


        // Show paired devices
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        listView.setAdapter(btArrayAdapter);

        listView.setOnItemClickListener(new myOnItemClickListener());
    }
    public void onClickButtonPaired() {
        btArrayAdapter.clear(); //리스트 뷰를 클리어
        if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
            deviceAddressArray.clear();
        }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }
    }

    public void onClickButtonSearch() {
        // Check if the device is already discovering
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                btArrayAdapter.clear();
                if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
                    deviceAddressArray.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
            } else {
                Toast.makeText(getApplicationContext(), "bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onClickButtonNext() {
        if(temp_name == null) {
            Toast.makeText(getApplicationContext(),"블루투스를 페어링 해주세요",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),temp_name+"에 연결",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), serviceActivity.class);
            intent.putExtra("bluetooth_address",temp_addres);
            startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    public class myOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //리스트에 아이템을 선택했을 때 발생하는 이벤트
            Toast.makeText(getApplicationContext(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

            textStatus.setText("try...");

            final String name = btArrayAdapter.getItem(position); // get name
            final String address = deviceAddressArray.get(position); // get address
            temp_name = name;
            temp_addres = address;

            textStatus.setText("connected to "+name);
            Intent intent = new Intent(getApplicationContext(), serviceActivity.class);
            intent.putExtra("bluetooth_address",address);
            startActivity(intent);

//            // create & connect socket
//            try {
//                btSocket = createBluetoothSocket(device);
//                btSocket.connect();
//            } catch (IOException e) {
//                flag = false;
//                textStatus.setText("connection failed!");
//                e.printStackTrace();
//            }
//
//            // start bluetooth communication
//            if(flag){
//                textStatus.setText("connected to "+name);
//                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//                intent.putExtra("bluetooth_address",address);
//                startActivity(intent);
//            }

        }

}

}