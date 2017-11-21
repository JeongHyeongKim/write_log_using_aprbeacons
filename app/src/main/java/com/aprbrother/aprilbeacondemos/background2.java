package com.aprbrother.aprilbeacondemos;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public Region region1 = new Region("Missing1","B5B182C7-EAB1-4988-AA99-B5C1517008D9", 1, 2); // FE07
    public Region region2 = new Region("Missing2", "B5B182C7-EAB1-4988-AA99-B5C1517008D9", 3, 4); //FF19
    public Region region3 = new Region("Missing3", "B5B182C7-EAB1-4988-AA99-B5C1517008D9", 5, 6); //FE87
    public Region region4 = new Region("Missing4", "B5B182C7-EAB1-4988-AA99-B5C1517008D9", 7, 8); //FF0F
    public Region region5 = new Region("Missing5", "B5B182C7-EAB1-4988-AA99-B5C1517008D9", 9, 10); //FE88
    public Region region6 = new Region("Missing6", "B5B182C7-EAB1-4988-AA99-B5C1517008D9", 11, 12); //FD78 , 비콘리스트 추가 요망
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
    ServiceThread thread;

    public background2() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);


        return START_NOT_STICKY;
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
       // beaconList=(BeaconList)getApplicationContext();
        //user_info=(insert_user_information)getApplicationContext();
        share_data=getSharedPreferences("share_data", 0);
        String store_name = share_data.getString("share_name", "");
        String store_location = share_data.getString("share_location", "");
        String store_number = share_data.getString("share_number", "");

        user_name=store_name;
        user_location=store_location;
        user_phone=store_number; // 이거를 userinfomation  class에서 폰번 받아오기 매서드로 교체한 다음에 불러오는 식으로 합시다!

        beaconManager=new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback(){
            public void onServiceReady(){
                try {
                    beaconManager.startMonitoring(region1);
                    beaconManager.startMonitoring(region2);
                    beaconManager.startMonitoring(region3);
                    beaconManager.startMonitoring(region4);
                    beaconManager.startMonitoring(region5);
                    beaconManager.startMonitoring(region6);
                    beaconManager.setBackgroundScanPeriod(5000, 100000); // 5초 스캔, 100초 쉼
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        final String getTime = sdf.format(date);
        beaconManager.setBackgroundScanPeriod(5000, 100000);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener(){

            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                for (int i = 0; i < list.size(); i++) {
                    if (region.getIdentifier().equals(region1.getIdentifier())) {
                        if (region.getMajor()==list.get(i).getMajor()) {
                            data1 = "Number"+list.get(i).getMajor()+" Beacon was near "+user_name+"("+user_phone+")"+" at"+getTime;
                            try {
                                background2.PHPRequest request = new background2.PHPRequest("http://beaconplus.co.kr/insert.php");
                                String result=request.PhPtest(String.valueOf(data1),String.valueOf(list.get(i).getMajor()));
                                Log.i("Background", "sended!");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (region.getIdentifier().equals(region2.getIdentifier())) {
                        if (region.getMajor()==list.get(i).getMajor()) {
                            data2 = "Number"+list.get(i).getMajor()+" Beacon was near "+user_name+" at"+getTime;
                            try {
                                background2.PHPRequest request = new background2.PHPRequest("http://beaconplus.co.kr/insert.php");
                                String result=request.PhPtest(String.valueOf(data2),String.valueOf(list.get(i).getMajor()));
                                Log.i("Background", "sended!");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    if (region.getIdentifier().equals(region3.getIdentifier())) {
                        if (region.getMajor()==list.get(i).getMajor()) {
                            data3 = "Number"+list.get(i).getMajor()+" Beacon was near "+user_name+"("+user_phone+")"+" at"+getTime;
                            Log.i("Beacon", "Discovered");
                            try {
                                background2.PHPRequest request = new background2.PHPRequest("http://beaconplus.co.kr/insert.php");
                                String result=request.PhPtest(String.valueOf(data3),String.valueOf(list.get(i).getMajor()));
                                Log.i("Background", "sended!3");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    if (region.getIdentifier().equals(region4.getIdentifier())) {
                        if (region.getMajor()==list.get(i).getMajor()) {
                            data4 = "Number"+list.get(i).getMajor()+" Beacon was near "+user_name+"("+user_phone+")"+" at"+getTime;
                            try {
                                background2.PHPRequest request = new background2.PHPRequest("http://beaconplus.co.kr/insert.php");
                                String result=request.PhPtest(String.valueOf(data4),String.valueOf(list.get(i).getMajor()));
                                Log.i("Background", "sended!");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    if (region.getIdentifier().equals(region5.getIdentifier())) {
                        if (region.getMajor()==list.get(i).getMajor()) {
                            data5 = "Number"+list.get(i).getMajor()+" Beacon was near "+user_name+"("+user_phone+")"+" at"+getTime;
                            try {
                                background2.PHPRequest request = new background2.PHPRequest("http://beaconplus.co.kr/insert.php");
                                String result=request.PhPtest(String.valueOf(data5),String.valueOf(list.get(i).getMajor()));
                                Log.i("Background", "sended!");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    if (region.getIdentifier().equals(region6.getIdentifier())) {
                        if (region.getMajor()==list.get(i).getMajor()) {
                            data6 = "Number"+list.get(i).getMajor()+" Beacon was near "+user_name+"("+user_phone+")"+" at"+getTime;
                            try {
                                background2.PHPRequest request = new background2.PHPRequest("http://beaconplus.co.kr/insert.php");
                                String result=request.PhPtest(String.valueOf(data6),String.valueOf(list.get(i).getMajor()));
                                Log.i("Background", "sended!");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            }

            @Override
            public void onExitedRegion(Region region) {

            }
        });
    }
    public class PHPRequest {
        private URL url;

        public PHPRequest(String url) throws MalformedURLException { this.url = new URL(url); }

        private String readStream(InputStream in) throws IOException {
            StringBuilder jsonHtml = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = null;

            while((line = reader.readLine()) != null)
                jsonHtml.append(line);

            reader.close();
            return jsonHtml.toString();
        }

        public String PhPtest(final String where, final String name) { // sql의 where은 문장으로 몇번비콘이 어떤사람 근처(폰번)에 몇시몇분에 있었다의 형식을 띌 예정
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss"); //날짜를 정수형으로 받아서 비교연산 후 php에서 차례대로 값이 들어가고 다차있을 시 가장 작은 값
            //에서부터 갱신 되도록 설정할거임
            final String getTime = sdf.format(date); // 현재 날짜 가져오기
            Log.i("date", getTime);
            try {
                String postData = "where=" + where+"&"+"name="+name+"&"+"when="+getTime;
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                String result = readStream(conn.getInputStream());
                conn.disconnect();
                return result;
            }
            catch (Exception e) {
                Log.i("PHPRequest", "request was failed.");
                return null;
            }
        }
    }
}
