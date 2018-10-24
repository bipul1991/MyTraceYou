package com.myapp.bipul.traceyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.myapp.bipul.traceyou.Helper.AllApis;
import com.myapp.bipul.traceyou.Database.LocalDb;
import com.myapp.bipul.traceyou.Helper.Help;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bipul on 12-Feb-18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService

{
    LocalDb localDb;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;
    Help help;
    public static String Fcm_Token="";

    static final String ACTION_START = "com.example.mobot.ecoline.SendLocService.ACTION_START";

    private static final String ACTION_IN_LOC = "com.example.mobot.ecoline";
    private static final String ACTION_IN_LOC_SER = "com.example.mobot.ecoline.service";

   public MyFirebaseMessagingService()
    {}

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
      //  Log.i("messageCheck", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
      /*  if (remoteMessage.getData().size() > 0) {
            Log.i("messageCheck", "Message data payload: " + remoteMessage.getData());
        }*/

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
         //   Log.i("messageCheck", "Message Notification Body: " + remoteMessage.getNotification().getBody());


            Intent intent1 = new Intent(MyFirebaseMessagingService.this, Geo.class);
            startService(intent1);

            checkMsg(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }

    public void notifyUser(String from, String notification)
    {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        MyNotificationManager myNotificationManager= new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(from,notification,intent);
    }


    public void checkMsg(String fcmMsg)
    {
        AllApis allApis = new AllApis();
       String fcmUrl =allApis.fcmUrl;

       String tag="", name="",number="",fcmToken="",imgLink="", titel="",message="",getlat="",getlng="";

        try {

            JSONObject jsonObject = new JSONObject(fcmMsg);
            tag= jsonObject.getString("tage");
            name= jsonObject.getString("name");
            number= jsonObject.getString("number");
            imgLink= jsonObject.getString("imgLink");
            message= jsonObject.getString("msg");
            fcmToken= jsonObject.getString("token");
            getlat= jsonObject.getString("lat");
            getlng= jsonObject.getString("lng");

            if(!number.contains("+"))
            {
                number = number.trim();
                number = "+"+number;
            }

            localDb = new LocalDb(MyFirebaseMessagingService.this);
/*
           boolean b1= localDb.getExistNumber(number);
           boolean b2= localDb.getSingleFrndReq(number);
           String b3 =  tag;*/



         //   if (!localDb.getExistNumber(number) & localDb.getSingleFrndReq(number) & tag.equalsIgnoreCase("rqLocation"))
            if ( tag.equalsIgnoreCase("rqLocation") &( !localDb.getExistNumber(number)) &( !localDb.getSingleFrndReq(number)))
            {
                help= new Help(MyFirebaseMessagingService.this);

                String address = help.getAddress(new LatLng(Double.parseDouble(getlat),Double.parseDouble(getlng)));
                localDb.insertFreq(number,name,imgLink,address,"active");// insert Address fromm Geo coder class
                notifyUser("New Friend Request",name+" want to see your lcation"); // creating notification
            }


            else if( tag.equalsIgnoreCase("rqLocation"))
                {
                   // notifyUser(name+" ("+number+")",name+" Is Tracing you...");
                    sharedPreferences =getSharedPreferences("traceYou", Context.MODE_PRIVATE);
                    databaseReference = FirebaseDatabase.getInstance().getReference(number).child("friends").child(number);
                    final String finalNumber = number;
                    final String finalFcmToken = fcmToken;
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {

                           String block ="no";
                           try {
                              block = dataSnapshot.child("block").getValue().toString();
                               Log.d("blockFrd","... "+block);

                           }
                           catch (Exception ex)
                           {

                           }


                           if(block.equalsIgnoreCase("no"))
                           {
                               try {
                                   sndLoc("sendLocation","success", finalFcmToken);

                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }
                           }

                           else
                               {
                                   try {
                                       sndLoc("error","you have blocked by "+ finalNumber,finalFcmToken);
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }
                               }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });

                }

                else if(tag.equalsIgnoreCase("sendLocation"))
                {
                    // Active Broadcast Receiver
                    Intent broadCastIntent = new Intent();
                    broadCastIntent.setAction(ACTION_IN_LOC);  // change package name
                    broadCastIntent.putExtra("number",number);
                    broadCastIntent.putExtra("lat",getlat);
                    broadCastIntent.putExtra("lng",getlng);
                    broadCastIntent.putExtra("tag",tag);
                    broadCastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(broadCastIntent);

                }

                else if(tag.equalsIgnoreCase("error"))
                {
                    Toast.makeText(MyFirebaseMessagingService.this,"Sorry your friend has blocked you",Toast.LENGTH_LONG).show();
                }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    // Method for sending location as notification
    public  void sndLoc(String tag,String msg,String token) throws JSONException {


        Help help = new Help(MyFirebaseMessagingService.this);
        Geo geo = new Geo();
        String lat = String.valueOf(geo.currentLatitude);
        String lng = String.valueOf(geo.currentLongitude);
      //  LatLng sndLtng = new LatLng(lat,lng);

            JSONObject jsonObject1 =help.makeJson(tag,"",msg,"",
                    sharedPreferences.getString("number",""),sharedPreferences.getString("fcmId",""),"",lat,lng);

 //       Log.i("tokenChecking2", "sendingNoty to: "+token);

            help.sendLocation(token,jsonObject1.toString());




      /*  Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(ACTION_IN_LOC_SER);  // change package name

        broadCastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(broadCastIntent);
        Fcm_Token=token;*/
    }


}
