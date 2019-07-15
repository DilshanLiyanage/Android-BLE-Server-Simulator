package com.example;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVERABLE_BT = 0;

    private Button btBtn;
    private TextView btStatus;
    private EditText payloadText;
    private Button sendPayload;

    //BLE Adapter
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btBtn = (Button)findViewById(R.id.btConnectBtn);
        btStatus = (TextView)findViewById(R.id.btStatus);
        payloadText = (EditText)findViewById(R.id.payloadText);
        sendPayload = (Button)findViewById(R.id.sendPayload);


        if(!btAdapter.isEnabled()){
            btStatus.setText("Not Enabled");
        }else{
            btStatus.setText("Enabled");
        }

        //enable Bluetooth
        btBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btAdapter.isEnabled()){
                    //enable BLE
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                    Intent enableDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(enableDiscoverable, REQUEST_DISCOVERABLE_BT);

                    btStatus.setText("Enabled");

                }else{
                    btStatus.setText("Enabled");
                }

            }
        });

        //send payload to app
        sendPayload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Advertise To All BLE Clients
                advertise();


//                String typedText = payloadText.getText().toString();
//
//                String []payloads = typedText.split("\n");
//                List<String> payloadsTrimed = new ArrayList();
//
//                for(String tem : payloads){
//                    if(tem.trim().length() != 0){
//                        payloadsTrimed.add(tem.trim());
//                    }
//                }
//
//                ArrayList<String> payloadChunck = new ArrayList<>();
//                if(payloadsTrimed.contains("/0")){
//
//                        for(int i=1; i<=5; i++){
//                            payloadChunck.add(payloadsTrimed.get(i-1));
//                            sendPayload(payloadChunck);
//                            payloadChunck.clear();
//                        }
//
//                        for(int p=6; p<=10; p++){
//                            payloadChunck.add(payloadsTrimed.get(p-1));
//                            sendPayload(payloadChunck);
//                            payloadChunck.clear();
//                        }
//
//
//                    }
//
                }



            });

    }


    //Advertise To All BLE Clients
    public void advertise(){

        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( false )
                .build();

        ParcelUuid pUuid = new ParcelUuid( UUID.fromString( getString( R.string.ble_uuid ) ) );

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( true )
                .addServiceData( pUuid, "a".getBytes( Charset.forName( "UTF-8" ) ) )
                .build();


        //.addServiceData( pUuid, "a".getBytes( Charset.forName( "UTF-8" ) ) )
        //addServiceUuid(ParcelUuid.fromString(SERVICE_DEVICE_INFORMATION.toString()))
        //.addServiceUuid( pppp )

        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };

        advertiser.startAdvertising( settings, data, advertisingCallback );

    }



    //send payload to paired device
    public void sendPayload(ArrayList<String> payloadList){

    }

}