package com.aprbrother.aprilbeacondemos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aprbrother.aprilbeacondemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hp on 2017-09-03.
 */

public class show_where_list extends Activity{ // 근방의 미아 최근 이동경로 조회

    String where1, where2, where3, where4,where5, when1, when2, when3, when4, when5;
    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    private int major;
    String mJsonString;

        //public BeaconList beaconList;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //beaconList=(BeaconList)getApplicationContext();
            Intent intent = getIntent();
            Data data = (Data)intent.getSerializableExtra("data");
            major=data.major;
        setContentView(R.layout.show_where_list);
        one=(TextView)findViewById(R.id.one);
        two=(TextView)findViewById(R.id.two);
        three=(TextView)findViewById(R.id.three);
        four=(TextView)findViewById(R.id.four);
        five=(TextView)findViewById(R.id.five);

        GetData task = new GetData();
        task.execute("http://beaconplus.co.kr/load_data.php?major="+major);


    }
    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(show_where_list.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            one.setText(result);
            Log.d("phptest_MainActivity", "response  - " + result);

            if (result == null){

                one.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("phptest_MainActivity", "response code - " + responseStatusCode);

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

                Log.d("phptest_MainActivity", "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                where1=item.getString("where1");
                where2=item.getString("where2");
                where3=item.getString("where3");
                where4=item.getString("where4");
                where5=item.getString("where5");
                when1=item.getString("when1");
                when2=item.getString("when2");
                when3=item.getString("when3");
                when4=item.getString("when4");
                when5=item.getString("when5");
            }
            one.setText(where1+when1);
            two.setText(where2+when2);
            three.setText(where3+when3);
            four.setText(where4+when4);
            five.setText(where5+when5);



        } catch (JSONException e) {
            Log.d("phptest_MainActivity", "showResult : ", e);
            e.printStackTrace();
        }

    }



    public void back_clicked (View v){
        Intent intent = new Intent(show_where_list.this, BeaconList.class);
        startActivity(intent);

    }




}

