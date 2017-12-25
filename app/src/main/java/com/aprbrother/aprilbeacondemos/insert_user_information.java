package com.aprbrother.aprilbeacondemos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aprbrother.aprilbeacondemo.R;

/**
 * Created by hp on 2017-08-29.
 */

public class insert_user_information extends Activity{
    EditText editname;
    EditText editPhoneNumber;
    EditText editLocation;
    TextView nowname;
    TextView nowlocation;
    TextView nownumber;
    static public String User_Name;
    //static public String User_Number;
    static public String User_Location;
    SharedPreferences share_data;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_user_info);

        editname=(EditText)findViewById(R.id.editName);
        editLocation=(EditText)findViewById(R.id.EditLocation);
        //editPhoneNumber=(EditText)findViewById(R.id.EditPhone);

        nowname=(TextView)findViewById(R.id.now_name);
        nowlocation=(TextView)findViewById(R.id.now_location);
       // nownumber=(TextView)findViewById(R.id.now_number);


        share_data=getSharedPreferences("share_data", 0);
        String store_name = share_data.getString("share_name", User_Name);
        String store_location = share_data.getString("share_location", User_Location);
        //String store_number = share_data.getString("share_number", User_Number);

        nowname.setText(store_name);
        nowlocation.setText(store_location);
       // nownumber.setText(store_number);
    }

    public void insert_clicked (View v){
        share_data=getSharedPreferences("share_data", 0);
        User_Name=editname.getText().toString();
        User_Location=editLocation.getText().toString();
        //User_Number=editPhoneNumber.getText().toString();
        SharedPreferences.Editor edit_data = share_data.edit();

        edit_data.putString("share_name",User_Name);
        edit_data.putString("share_location",User_Location);
        //edit_data.putString("share_number",User_Number);
        edit_data.commit();

        Intent intent = new Intent(insert_user_information.this, BeaconList.class);
        startActivity(intent);
    }
}
