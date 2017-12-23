package com.aprbrother.aprilbeacondemos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
 * Created by jeonghyeongkim on 2017. 12. 22..
 */

public class Search_data extends Activity{
    String where1, where2, where3, where4,where5;
    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    String mJsonString;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_data);

    }

    private class GetData extends AsyncTask<String, Void, String> { // 사용자로부터 알고있는 비콘 정보를 입력받아서 최근 5개의 위치를 조회한다.
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Search_data.this,
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

            }
            one.setText(where1);
            two.setText(where2);
            three.setText(where3);
            four.setText(where4);
            five.setText(where5);

        } catch (JSONException e) {
            Log.d("phptest_MainActivity", "showResult : ", e);
            e.printStackTrace();
        }

    }
    public void search_clicked(){


    }
    public void back_clicked(){
        Intent intent = new Intent(Search_data.this, BeaconList.class);
    }
}
