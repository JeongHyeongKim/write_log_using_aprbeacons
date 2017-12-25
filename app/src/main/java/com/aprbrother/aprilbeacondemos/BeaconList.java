package com.aprbrother.aprilbeacondemos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aprbrother.aprilbeacondemo.R;
import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.BeaconManager;
import com.aprilbrother.aprilbrothersdk.BeaconManager.RangingListener;
import com.aprilbrother.aprilbrothersdk.Region;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 搜索展示beacon列表 scan beacon show beacon list
 */
public class BeaconList extends Activity {
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final String TAG = "BeaconList";
    public Region region1 = new Region("ROOT","B5B182C7-EAB1-4988-AA99-B5C1517008D9", 22, 22); // FE07
    public Region region2 = new Region("a", "B5B182C7-EAB1-4988-AA99-B5C1517008D9",1, 1 );



    private BeaconAdapter adapter;
    private BeaconManager beaconManager;
    private ArrayList<Beacon> myBeacons;
    public static int send_major=0;
    public int[] flag=new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(BeaconList.this, background2.class);
        startService(intent);
        init();
    }

    private void init() {
        myBeacons = new ArrayList<Beacon>();
      //  asdf = new ArrayList<Beacon>();
        ListView lv = (ListView) findViewById(R.id.lv);
        adapter = new BeaconAdapter(this);
        lv.setAdapter(adapter);
        AprilL.enableDebugLogging(true);
        beaconManager = new BeaconManager(getApplicationContext());

         beaconManager.setForegroundScanPeriod(5000, 1000);
        beaconManager.setRangingListener(new RangingListener() {

            @Override
            public void onBeaconsDiscovered(Region region,
                                            final List<Beacon> beacons) {

                //Log.i(TAG, "onBeaconsDiscovered: ");
                for (Beacon beacon : beacons) {

                    if(beacon.getMajor()<101){
                        if(beacon.getMajor()==22){
                            if(flag[0]>0){
                                for(int i=0;i<myBeacons.size();i++){
                                    if(myBeacons.get(i).getMajor()==beacon.getMajor()){
                                        myBeacons.set(i, beacon);
                                    }
                                }
                            }else{

                                myBeacons.add(beacon);
                                flag[0]++;
                                Log.i(TAG, "add!" + beacon.getMajor());
                                Log.i(TAG, "Info" + myBeacons.size());
                            }
                        }



                    }

                }

                ComparatorBeaconByRssi com = new ComparatorBeaconByRssi();
                Collections.sort(myBeacons, com);
                adapter.replaceWith(myBeacons);

            }
        });


        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Beacon beacon = myBeacons.get(arg2);
                send_major=beacon.getMajor();
                Data data = new Data(send_major);
                Intent intent = new Intent(BeaconList.this, show_where_list.class);
                intent.putExtra("data", data);
                /*if (beacon.getName().contains("ABSensor")) {
                    intent = new Intent(BeaconList.this, SensorActivity.class);
                } else {
                    intent = new Intent(BeaconList.this, ModifyActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("beacon", beacon);
                intent.putExtras(bundle);*/
                startActivity(intent);
            }
        });




    }


    private void connectToService() {
        Log.i(TAG, "connectToService");
        adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    Log.i(TAG, "connectToService");
                    beaconManager.startRanging(region1);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG)
                        .show();
                getActionBar().setSubtitle("Bluetooth not enabled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        // if (!beaconManager.hasBluetooth()) {
        // Toast.makeText(this, "Device does not have Bluetooth Low Energy",
        // Toast.LENGTH_LONG).show();
        // Log.i(TAG, "!hasBluetooth");
        // return;
        // }
        // if (!beaconManager.isBluetoothEnabled()) {
        // Log.i(TAG, "!isBluetoothEnabled");
        // Intent enableBtIntent = new Intent(
        // BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        // } else {
        // connectToService();
        // }
        connectToService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        try {
            myBeacons.clear();
            beaconManager.stopRanging(region1);

            beaconManager.disconnect();
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }
        super.onStop();
    }

    public void scanner_is_clicked(View v){
        Intent intent = new Intent(BeaconList.this, insert_user_information.class);
        startActivity(intent);
    }

    public void Search_clicked(View v){
        Intent intent = new Intent(BeaconList.this, Search_data.class);
        startActivity(intent);
    }

}
