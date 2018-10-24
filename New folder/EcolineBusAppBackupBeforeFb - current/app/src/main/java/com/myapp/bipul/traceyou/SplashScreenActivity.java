package com.myapp.bipul.traceyou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences sp;

    protected static final String TAG = "LocationOnOff";
    final static int REQUEST_LOCATION = 199;
    public LocationManager locationManager;
    Intent globIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent1 = new Intent(SplashScreenActivity.this, Geo.class);
       startService(intent1);
    // stopService(intent1);


        Intent intent2 = new Intent(SplashScreenActivity.this, SendLocService.class);
       // startService(intent2);
      stopService(intent2);


        sp = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        //displayLocationSettingsRequest(SplashScreenActivity.this);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    if(sp.getString("number","").isEmpty())
                    {

                            globIntent = new Intent(SplashScreenActivity.this, OtpActivity.class);
                            startActivity(globIntent);
                            finish();

                    }
                    else
                        {
                            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                globIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                startActivity(globIntent);
                                finish();
                            }

                            else {
                                displayLocationSettingsRequest(SplashScreenActivity.this);
                            }

                        }



                }
            }
        };
        timerThread.start();
    }

    public void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SplashScreenActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;


                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  Log.d("locationCode",".. "+requestCode+"..."+resultCode);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        //   Log.d("locationCode"," turned on "+requestCode+"..."+resultCode);
                        Intent intent1 = new Intent(SplashScreenActivity.this, Geo.class);
                        startService(intent1);

                        globIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(globIntent);
                        finish();

                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        // Log.d("locationCode"," turned off "+requestCode+"..."+resultCode);
                        finish();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }
}

// http://schoolradius.net/echoline/