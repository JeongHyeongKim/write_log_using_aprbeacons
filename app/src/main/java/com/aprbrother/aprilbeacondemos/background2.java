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
    public Region region1 = new Region("충북대","B5B182C7-EAB1-4988-AA99-B5C1517008D9", 22, 22); // FDFB
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
       // String store_location = share_data.getString("share_location", "");
        String store_number = share_data.getString("share_number", "");

        user_name=store_name;
        //user_location=store_location;
        user_phone=store_number; // 이거를 userinfomation  class에서 폰번 받아오기 매서드로 교체한 다음에 불러오는 식으로 합시다!

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
                                data1 = "Number" + list.get(i).getMajor() + "%20was%20near%20ROOT%20" + user_name+ "%20at%20" + getTime;
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
   // public class PHPRequest {
    //    private URL url;

      //  public PHPRequest(String url) throws MalformedURLException { this.url = new URL(url); }

        //private String readStream(InputStream in) throws IOException {
         //   StringBuilder jsonHtml = new StringBuilder();
          //  BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
           // String line = null;

          //  while((line = reader.readLine()) != null)
           //     jsonHtml.append(line);

//            reader.close();
  //          return jsonHtml.toString();
    //    }

      //  public String PhPtest(final String where, final int name) { // sql의 where은 문장으로 몇번비콘이 어떤사람 근처(폰번)에 몇시몇분에 있었다의 형식을 띌 예정
        //    long now = System.currentTimeMillis();
         //   Date date = new Date(now);
          //  SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss"); //날짜를 정수형으로 받아서 비교연산 후 php에서 차례대로 값이 들어가고 다차있을 시 가장 작은 값
            //에서부터 갱신 되도록 설정할거임
          //  final String getTime = sdf.format(date); // 현재 날짜 가져오기
           // Log.i("date", getTime);
           // try {
            //    String postData = "where=" + where+"&"+"name="+name+"&"+"when="+getTime;
             //   Log.i("test", "test");
              //  HttpURLConnection conn = (HttpURLConnection)url.openConnection();
               // Log.i("test", "test");
               // conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
               // Log.i("test", "test");
               // conn.setRequestMethod("GET");
               // Log.i("test", "test");

             //   conn.setConnectTimeout(5000);
             //   Log.i("test", "test");
             //   conn.setDoOutput(true);
              //  Log.i("test", "test");
              //  conn.setDoInput(true);
              //  Log.i("test", "test");
              //  OutputStream outputStream = conn.getOutputStream(); // 오류
               // Log.i("test", "test");
               // outputStream.write(postData.getBytes("UTF-8"));
               // Log.i("test", "test");

             //   outputStream.flush();
              //  outputStream.close();
              //  String result = readStream(conn.getInputStream());
              //  conn.disconnect();
              //  return result;
           // }
           // catch (Exception e) {
           //     Log.i("PHPRequest1", "request was failed.");
           //     return null;
           // }
      //  }
   // }

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
