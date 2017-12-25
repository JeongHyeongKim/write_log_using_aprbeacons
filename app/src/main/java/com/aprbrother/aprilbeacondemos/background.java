package com.aprbrother.aprilbeacondemos;

import android.app.Application;
import android.content.Intent;
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

/**
 근처에 있는 비콘들 모두 검색 후 이 비콘의 위치를
 */

public class background extends Application {
    private BeaconManager beaconManager;
    private BeaconAdapter adapter;
    private ArrayList<Beacon> myBeacon;
    public BeaconList beaconList;
    public insert_user_information user_info;
    private Region region1;
    private Region region2;
    private Region region3;
    private Region region4;
    private Region region5;
    private Region region6;
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



    public IBinder onBind(Intent intent){
        return null;
    }

    public void onCreate(){ // 가장 먼저 호출되는 매서드
        super.onCreate();
        beaconList=(BeaconList)getApplicationContext();
        user_info=(insert_user_information)getApplicationContext();
        region1=beaconList.region1;

        user_name=user_info.User_Name;
        user_location=user_info.User_Location;
        //user_phone=user_info.User_Number; // 이거를 userinfomation  class에서 폰번 받아오기 매서드로 교체한 다음에 불러오는 식으로 합시다!

        beaconManager=new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback(){
            public void onServiceReady(){
                try {
                    beaconManager.startMonitoring(region1);

                    beaconManager.setBackgroundScanPeriod(5000, 100000); // 5초 스캔, 100초 쉼
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
        init();
    }

     private void init(){
         long now = System.currentTimeMillis();
         Date date = new Date(now);
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
         final String getTime = sdf.format(date); // 현재 날짜 가져오기

         beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() { // 거리는 정확하지 않아서 제외

             @Override
             public void onEnteredRegion(Region region, List<Beacon> list) { // 모니터링 되면 서버로 데이터 전송
                 for (int i = 0; i < list.size(); i++) {
                     if (region.getIdentifier().equals(region1.getIdentifier())) {
                         if (region.getMajor()==list.get(i).getMajor()) {
                             data1 = "Number"+list.get(i).getMajor()+" Beacon was near "+user_name+"("+user_phone+")"+" at"+getTime;
                             try {
                                 PHPRequest request = new PHPRequest("http://beaconplus.co.kr/insert.php?");
                                 String result=request.PhPtest(String.valueOf(data1),list.get(i).getMajor());
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

        public String PhPtest(final String where, final int name) { // sql의 where은 문장으로 몇번비콘이 어떤사람 근처(폰번)에 몇시몇분에 있었다의 형식을 띌 예정
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //날짜를 정수형으로 받아서 비교연산 후 php에서 차례대로 값이 들어가고 다차있을 시 가장 작은 값
            //에서부터 갱신 되도록 설정할거임
            final String getTime = sdf.format(date); // 현재 날짜 가져오기
            try {
                String postData = "where=" + where+"&"+"name="+name+"&"+"when="+getTime;
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("GET");
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
