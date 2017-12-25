package com.aprbrother.aprilbeacondemos;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.BeaconManager;
import com.aprilbrother.aprilbrothersdk.Region;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class background2 extends Service {
    private BeaconManager beaconManager;
    private BeaconAdapter adapter;
    private ArrayList<Beacon> myBeacon;
    public BeaconList beaconList;
    public insert_user_information user_info;
    public Region region1 = new Region("Chicken","B5B182C7-EAB1-4988-AA99-B5C1517008D9", 22, 22); // FDFB
    private String data1;
    private String data2;
    private String data3;
    private String data4;
    private String data5;
    private String data6;
    private String who;
    private String user_name;
    private String user_location;
    private String user_phone;
    SharedPreferences share_data;
    final int flag=0;
    ServiceThread thread;

    public background2() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
    public void init(){
        Log.i("asdf", "start");

        share_data=getSharedPreferences("share_data", 0);
        String store_name = share_data.getString("share_name", "");
        String store_location = share_data.getString("share_location", "");

        user_name=store_name;
        user_location=store_location;


        beaconManager=new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback(){
            public void onServiceReady(){
                try {
                    beaconManager.startMonitoring(region1);
                    beaconManager.setBackgroundScanPeriod(5000, 5000); // 5초 스캔, 100초 쉼
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

        beaconManager.setBackgroundScanPeriod(5000, 100000);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener(){

            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                for (int i = 0; i < list.size(); i++) {
                    if (region.getIdentifier().equals(region1.getIdentifier())) {
                        if (region.getMajor()==list.get(i).getMajor()) {
                            if(flag!=1) {
                                long now = System.currentTimeMillis();
                                Date date = new Date(now);
                                SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
                                final String getTime = sdf.format(date); // 현재 날짜 가져오기
                                data1 = region.getIdentifier() + "%20was%20near%20" + user_location+ "%20at%20" + getTime;
                                SendData task = new SendData();
                                task.execute("http://beaconplus.co.kr/insert.php?name=" + list.get(i).getMajor() + "&" + "where=" + data1 + "&" + "when=" + getTime);
                            }
                            else{}
                        }
                    }
                }
            }

            @Override
            public void onExitedRegion(Region region) {

            }
        });
    }

    private class SendData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();



                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("PHP", "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                Log.d("PHP", "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

}
