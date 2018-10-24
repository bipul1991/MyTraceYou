package com.myapp.bipul.traceyou;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.bipul.traceyou.Fcm_Database_Helper.UserInfoModel;
import com.myapp.bipul.traceyou.Helper.NetworkUtlities;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    EditText nameEdt,emailEdt;
    Button submitBtn;
    String mobile, userName,userEmail;

    ArrayList phoneList;
    ArrayList nameList;
    SharedPreferences sharedPreferences;

    ArrayList<HashMap<String,String>> contactData;
    Geo geo;
    Intent globIntent;
    Intent intGet;
    String pNumber="";

    boolean nextPage = true;
    ProgressDialog pdLoading;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        nameEdt =(EditText)findViewById(R.id.nameEdt);
        emailEdt =(EditText)findViewById(R.id.emailEdt);
        submitBtn =(Button) findViewById(R.id.submitBtn);
        geo = new Geo();

        intGet =  getIntent();
        pNumber = intGet.getStringExtra("number");

      //  accessContact();
        sharedPreferences = getSharedPreferences("traceYou", Context.MODE_PRIVATE);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        String fcmId= sharedPreferences.getString("countryCode","");

        Log.d("codess", ".."+fcmId);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtlities.isConnected(ProfileActivity.this))
                {
                    if( nameEdt.getText().toString().trim().isEmpty())
                    {
                        Toast.makeText(ProfileActivity.this,"Please enter your name",Toast.LENGTH_LONG).show();
                    }
                    else {
                        saveUserInfo();
                    }
                }
            }
        });


    }

    private  void saveUserInfo()
    {
        pdLoading = new ProgressDialog(ProfileActivity.this);
        pdLoading.setMessage("\tUpdating your details...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        userName = nameEdt.getText().toString().trim();
        userEmail = emailEdt.getText().toString().trim();
        mobile =pNumber;
        String lat=String.valueOf(geo.currentLatitude);
        String lng=String.valueOf(geo.currentLongitude);
        final String imgLink="www.hjjfj";
        String time="ooo";
        String block="no";
     //   String fcmId="000008888999977";
        String fcmId= sharedPreferences.getString("fcmId","");

        UserInfoModel userInfoModel = new UserInfoModel(mobile,userName,userEmail,fcmId,lat,lng,imgLink,block,time);
        databaseReference.child(mobile).setValue(userInfoModel);
      //  databaseReference.child(mobile).child("friends").child("frnd").setValue( userInfoModel);

      //  Toast.makeText(ProfileActivity.this,"Info Saved",Toast.LENGTH_SHORT).show();

        Intent intent2 = new Intent(ProfileActivity.this, Geo.class);
        stopService(intent2);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                sharedPreferences = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("name", userName);
                edit.putString("imgLink", imgLink);
                edit.putString("number", pNumber);
                edit.commit();

                if (nextPage == true) {
                    nextPage=false;
                    //  Toast.makeText(ProfileActivity.this,"Add "+dataSnapshot.getKey()+" to UI after "+s,Toast.LENGTH_LONG).show();
pdLoading.dismiss();
                    Toast.makeText(ProfileActivity.this,"Sing in successfull",Toast.LENGTH_SHORT).show();

                    Intent intent1 = new Intent(ProfileActivity.this, Geo.class);
                    startService(intent1);

                    globIntent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(globIntent);
                    finish();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                pdLoading.dismiss();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                pdLoading.dismiss();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                pdLoading.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pdLoading.dismiss();
            }
        });

    }

  /*  private void getUserInfo()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference("89061065700").child("friends").child("frnd");
        databaseReference.child("email").setValue("@gmail.com");

    }*/

    //____________

}
