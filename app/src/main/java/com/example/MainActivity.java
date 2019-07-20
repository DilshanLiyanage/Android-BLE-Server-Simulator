package com.example;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {



    private Button btBtn;
    private TextView btStatus;
    private EditText payloadText;
    private Button sendPayload;

    private UUID SERVICE_UUID = UUID.fromString("CDB7950D-73F1-4D4D-8E47-C090502DBD63");
    private UUID CHARACTERISTIC_UUID = UUID.fromString( "CDB7950D-73F1-4D4D-8E47-C090501DBD64" );

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private BluetoothGattServer mGattServer;
    private BluetoothGattServerCallback mGattServerCallback;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btBtn = (Button)findViewById(R.id.btConnectBtn);
        btStatus = (TextView)findViewById(R.id.btStatus);
        payloadText = (EditText)findViewById(R.id.payloadText);
        sendPayload = (Button)findViewById(R.id.sendPayload);











        //enable Bluetooth
        btBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                mBluetoothAdapter = mBluetoothManager.getAdapter();

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
                btStatus.setText("Enabled");

                mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

            }


        });






        //send data to client
        sendPayload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupServer(payloadText.toString().trim());

            }

        });


    }


    public void setupServer(final String payload){
        mGattServerCallback = new BluetoothGattServerCallback() {



            @Override public void onCharacteristicReadRequest (
                    BluetoothDevice device, int requestId, int offset,
                    BluetoothGattCharacteristic characteristic ) {
                super.onCharacteristicReadRequest ( device, requestId, offset, characteristic );
                String string = payload;
                byte[] value = string.getBytes ( Charset.forName ( "UTF-8" ) );
                mGattServer.sendResponse ( device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value );
            }

        };

        mGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        setupService();
        startAdvertising();
    }




    public void setupService(){
        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_INDICATE, BluetoothGattCharacteristic.PERMISSION_READ);
        service.addCharacteristic(characteristic);
        mGattServer.addService(service);
    }





    private void startAdvertising() {
        if (mBluetoothLeAdvertiser == null) {
            return;
        }
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build();

        ParcelUuid parcelUuid = new ParcelUuid(SERVICE_UUID);
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(parcelUuid)
                .build();
        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            //Log.d(TAG, "Peripheral advertising started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            //Log.d(TAG, "Peripheral advertising failed: " + errorCode);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        stopAdvertising();
        stopServer();
    }
    private void stopServer() {
        if (mGattServer != null) {
            mGattServer.close();
        }
    }
    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }
    }








}