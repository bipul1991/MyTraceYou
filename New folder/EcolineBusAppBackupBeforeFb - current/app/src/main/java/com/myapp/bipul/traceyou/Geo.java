package com.myapp.bipul.traceyou;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.bipul.traceyou.Database.LocalDb;
import com.myapp.bipul.traceyou.Fcm_Database_Helper.UserFriendModel;
import com.myapp.bipul.traceyou.Helper.NetworkUtlities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by moboticstechnologies on 10/12/17.
 */

public class Geo extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Context mContext;
    public static String status="";
    public  static  boolean geoService=true;


    private TimerTask doAsynchronousTask;
    final Handler handler = new Handler();
    private Timer timer = new Timer();
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    static final Integer GPS_SETTINGS = 0x7;
    PendingResult<LocationSettingsResult> result;

    public static final String LOGIN_PREF = "LoginData";

    public static final String STATUS_PREF = "StatusPref";


    public static double currentLatitude=0;
    public static double currentLongitude=0;

    public LocationManager locationManager,locationManager22;
    public boolean permissionCheck = false;

    Location loc;

    String ip, restUrl = "cdr/storeGeolocation", user_id;
    private String provider_info;

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    Location location;

    //
    private GoogleApiClient client;
    private Location lastLocation;
    private LocationRequest locationRequest;
    public static final int REQUEST_LOCATION_CODE = 99;


    int svCount=0;
    String cCode="",mynumber="";
    SharedPreferences sp;
    DatabaseReference databaseReference;
    DatabaseReference mainDatabaseRef;
    LocalDb localDb;

    boolean locLoop = true;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {





//               Toast.makeText(Geo.this, "Please Check Your Internet Connection.", Toast.LENGTH_LONG).show();

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager22 = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

//        Toast.makeText(getApplicationContext(),"Geo Service Started",Toast.LENGTH_LONG).show();

        //getLocation();
        //  fn_getlocation();
     //   getLoc();
        callAsynchronousTask();

        mainDatabaseRef = FirebaseDatabase.getInstance().getReference();

       sp = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        cCode = sp.getString("countryCode","");
        mynumber = sp.getString("number","");
        localDb = new LocalDb(Geo.this);

        if(NetworkUtlities.isConnected(Geo.this)) {

            if(!mynumber.isEmpty())
            startTheard();
        }
        else
            {
                Toast.makeText(Geo.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
            }

        return Service.START_STICKY;
    }

    public void callAsynchronousTask() {
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

//                            Toast.makeText(getApplicationContext(),"in geo async",Toast.LENGTH_LONG).show();

                            if (NetworkUtlities.isConnected(Geo.this)) {

//                                Toast.makeText(getApplicationContext(),"in geo async if",Toast.LENGTH_LONG).show();

//                                Toast.makeText(getApplicationContext(),""+currentLatitude,Toast.LENGTH_LONG).show();

                                //                 getLocation();
                                getLoc();

                            }
                            else
                                {

                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, (1*30000)); //execute in every 1200000 ms // 2 minute
    }

    public void getLoc()
    {



        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
  //          Log.d("latLngg","inIF");

            if(locLoop == true) {

                buildGoogleApiClient();
                locLoop= false;
            }

           // mManagerFun();
        }
        else {

            locLoop = true;

 //           Log.d("latLngg","..else0");


 //           Log.d("latLngg","..else");




        }
}

private  void mManagerFun()
{
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
    }
    //  1000*60*30

    locationManager22.requestLocationUpdates(LocationManager.GPS_PROVIDER
            ,1000*60*30 , 100, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    Toast.makeText(Geo.this,"location Chnaged Network provider",Toast.LENGTH_LONG).show();

                    //          Log.d("loc1Nt", ".." + currentLatitude);
                    //        Log.d("loc1Nt", ".." + currentLongitude);

                    //   loc = location;

                    Log.d("locTestIf", "lat: " + currentLatitude + "  lng: " + currentLongitude);




                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
}


    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {


       // locationRequest = new LocationRequest();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(200);
     // locationRequest.setInterval(1000*60*30);
         locationRequest.setInterval(3*60000);
        locationRequest.setFastestInterval(5000);



        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==(PackageManager.PERMISSION_GRANTED)) {

            if(client.isConnected())
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
            }

            else
                {
              //  log("Google_Api_Client: It was NOT connected on (onConnected) function, It is definetly bugged.");

            }

    }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

    //  Toast.makeText(Geo.this,"location Chnaged Out Side",Toast.LENGTH_LONG).show();


   //     LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());



        //    Toast.makeText(Geo.this,"location Chnaged",Toast.LENGTH_LONG).show();

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

         updateLocation();

     //   Toast.makeText(Geo.this,"location Chnaged",Toast.LENGTH_LONG).show();

        //          Log.d("loc1Nt", ".." + currentLatitude);
        //        Log.d("loc1Nt", ".." + currentLongitude);

        //   loc = location;

       // Log.d("locTestIf", "lat: " + currentLatitude + "  lng: " + currentLongitude);


        if (NetworkUtlities.isConnected(Geo.this)) {


        //    new AddDetailServer().execute(ip + restUrl);

        } else {
            //  Toast.makeText(Geo.this, "Please Check Your Internet Connection.", Toast.LENGTH_LONG).show();

        }
        //  Toast.makeText(getApplicationContext(),""+currentLatitude,Toast.LENGTH_LONG).show();




    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    protected  synchronized void buildGoogleApiClient()
    {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    private void startTheard() //___________________________________// Access Contact List for search friend in FCM
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
                svCount = sp.getInt("count",0);
                Log.d("svCount","...."+svCount);
                ContentResolver cr =getContentResolver();
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);

                int  count = cursor.getCount();
              //  if (count > svCount) {
                    while (cursor.moveToNext()) {


                        try {
                            //  Thread.sleep(1000);

                            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                                while (phones.moveToNext()) {
                                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                       /* HashMap<String,String> map=new HashMap<String,String>();
                        map.put("name", name);
                        map.put("number", phoneNumber);*/
                                    //  Log.d("contactCheck","...."+name+"  Num:.."+phoneNumber);

                                    //  contactData.add(map);
                                    if(!phoneNumber.contains(cCode))
                                    {
                                        phoneNumber = cCode+phoneNumber;

                                    }
                                    if (!localDb.getExistNumber(phoneNumber)) {
                                        searchInFcm(phoneNumber, name);
                                    }
                                }
                                phones.close();

                            }

                            //    sharedPreferences = getSharedPreferences("traceYou", Context.MODE_PRIVATE);



                            //   Log.d("contactCheck",".."+contactData);


                        } catch (Exception e) {
                        }
                    }
                    //    Log.d("contactCheck","...."+contactData);

                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt("count", count);
                    edit.commit();


            }
        }).start();
    }



    public void searchInFcm(final String mobileId, final String name)
    {
        String number="";
       /* //  databaseReference = FirebaseDatabase.getInstance().getReference().child(mobileId);
        if(!mobileId.contains(cCode))
        {
            number = cCode+mobileId;

        }
        else
        {
            number = mobileId;
        }*/
        number = mobileId;

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(number);

        final String finalNumber = number;
    //    Log.d("countryNum",".."+finalNumber);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {




                if (dataSnapshot.getValue() != null) {

                  //  Toast.makeText(Geo.this, "ondata change", Toast.LENGTH_SHORT).show();

                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userEmail =dataSnapshot.child("email").getValue().toString();
                    String mobile =dataSnapshot.child("mobile").getValue().toString();
                    String lat=dataSnapshot.child("lat").getValue().toString();
                    String lng=dataSnapshot.child("lng").getValue().toString();
                    String imgLink=dataSnapshot.child("imglink").getValue().toString();
                    String time=dataSnapshot.child("time").getValue().toString();
                    //  String block=dataSnapshot.child("block").getValue().toString();
                    String fcmId=dataSnapshot.child("fcmId").getValue().toString();
                    //  num = dataSnapshot.child("email").getValue().toString();
                    Log.d("emailTest", "...." + userEmail + "  name: " + userName);

                //    boolean b2= localDb.getExistNumber(mobile);

                    if (!localDb.getExistNumber(mobile)) {
                        localDb.insertExist(mobile, name);

                    }
                //    boolean b1= localDb.getExistNumber(mobile);
                    UserFriendModel userInfoModel = new UserFriendModel(mobile,name,userEmail,fcmId,lat,lng,imgLink,time);
                    //  mainDatabaseRef.child("friends").child(finalNumber).setValue(userInfoModel);
                    mainDatabaseRef.child(mynumber).child("friends").child(mobile).setValue( userInfoModel);

                    //    Log.d("localDb","...."+(localDb.getExistNumber(mobileId)));
                }
                // Log.d("contactCheck","...."+num);

                //  databaseReference = FirebaseDatabase.getInstance().getReference("89061065700").child("friends").child("frnd");
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Geo.this,"on Cancelled",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateLocation()
    {
        // sharedPreferences =getSharedPreferences("traceYou", Context.MODE_PRIVATE);
       // String root = sharedPreferences.getString("number","");


        if(currentLatitude>0 &  !mynumber.isEmpty()) {
            databaseReference = FirebaseDatabase.getInstance().getReference(mynumber);

            databaseReference.child("lat").setValue(currentLatitude);
            databaseReference.child("lng").setValue(currentLongitude);
            databaseReference.child("time").setValue(getDateTime());
        }
    }

    String getDateTime()
    {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }
}

/*
"yyyy.MM.dd G 'at' HH:mm:ss z" ---- 2001.07.04 AD at 12:08:56 PDT
        "hh 'o''clock' a, zzzz" ----------- 12 o'clock PM, Pacific Daylight Time
        "EEE, d MMM yyyy HH:mm:ss Z"------- Wed, 4 Jul 2001 12:08:56 -0700
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ"------- 2001-07-04T12:08:56.235-0700
        "yyMMddHHmmssZ"-------------------- 010704120856-0700
        "K:mm a, z" ----------------------- 0:08 PM, PDT
        "h:mm a" -------------------------- 12:08 PM
        "EEE, MMM d, ''yy" ---------------- Wed, Jul 4, '01
*/
