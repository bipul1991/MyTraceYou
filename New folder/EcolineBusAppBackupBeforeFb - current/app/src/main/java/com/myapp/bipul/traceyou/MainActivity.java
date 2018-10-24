package com.myapp.bipul.traceyou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.bipul.traceyou.Helper.Help;
import com.myapp.bipul.traceyou.Helper.NetworkUtlities;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent intent;

    LinearLayout nearByLay,frndLay;

    final static int REQUEST_LOCATION = 199;
    protected static final String TAG = "LocationOnOff";
    Help help;
    Geo geo;
    AdView adView;
    AdRequest adRequest;

    ImageView sosImg;
    TextView nameTxt,mobileTxt;
    Button nearByLcnBtn,frndLocBtn;

    Intent globIntent;

    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    boolean exit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle("");

        View headerView = navigationView.getHeaderView(0);

    //   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

      /*  fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

       /* Intent intent1 = new Intent(MainActivity.this, Geo.class);
        startService(intent1);*/

        help = new Help(MainActivity.this);
        geo = new Geo();

        sharedPreferences =getSharedPreferences("traceYou", Context.MODE_PRIVATE);
       // String root = sharedPreferences.getString("number","");

        sosImg = (ImageView)findViewById(R.id.sosImg);

        mobileTxt = (TextView)headerView.findViewById(R.id.mobileTxt);
        nameTxt = (TextView)headerView.findViewById(R.id.nameTxt);


        nameTxt.setText(sharedPreferences.getString("name","no name"));
        mobileTxt.setText(sharedPreferences.getString("number",""));


        nearByLay = (LinearLayout)findViewById(R.id.nearByLay);
        frndLay = (LinearLayout)findViewById(R.id.frndLay);
        frndLocBtn = (Button) findViewById(R.id.frndLocBtn);
        nearByLcnBtn = (Button) findViewById(R.id.nearByLcnBtn);

        nearByLcnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this,NearByPlaceActivity.class);
                startActivity(intent);
            }
        });

        frndLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this,FriendListActivity.class);
                startActivity(intent);
            }
        });

        adView = (AdView)findViewById(R.id.adView);
      // MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
     MobileAds.initialize(this, "ca-app-pub-1806296421186622~2732604891");

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);





        if(NetworkUtlities.isConnected(MainActivity.this))
        {
            updateLocation();
            if(!help.statusCheck())
                displayLocationSettingsRequest(MainActivity.this);
        }

        else
        {
            Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
             finish();
        }




        sosImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "We are working on it you will get this facility in next update", Toast.LENGTH_SHORT).show();
            }
        });


        nearByLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,NearByPlaceActivity.class);
                startActivity(intent);
                // finish();
            }
        });
        frndLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(MainActivity.this,FriendListActivity.class);
                startActivity(intent);
               // // finish();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.shear_friends) {

            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, "Hey! you can trace your family member/friend's current location  by \"Trace you\" \nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));

            return true;
        }
        else if(id == R.id.nav_help)
        {
            globIntent =  new Intent(MainActivity.this,HelpActivity.class);
            startActivity(globIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_find_friends) {

            globIntent =  new Intent(MainActivity.this,FriendListActivity.class);
            startActivity(globIntent);
            // finish();

        } else if (id == R.id.nav_nearby_place)
        {
            globIntent =  new Intent(MainActivity.this,NearByPlaceActivity.class);
            startActivity(globIntent);
            // finish();
        }
        else if (id == R.id.nav_share) {

            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, "Hey! you can trace your family member/friend's current location  by \"Trace you\" \nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));

        } else if (id == R.id.nav_about_us) {

            globIntent =  new Intent(MainActivity.this,AboutUsActivity.class);
            startActivity(globIntent);

        }

        else if(id == R.id.nav_help)
        {
            globIntent =  new Intent(MainActivity.this,HelpActivity.class);
            startActivity(globIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
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

   public void updateLocation()
    {
     /*  // sharedPreferences =getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        String root = sharedPreferences.getString("number","");
        databaseReference = FirebaseDatabase.getInstance().getReference(root);
        databaseReference.child("lat").setValue(geo.currentLatitude);
        databaseReference.child("lng").setValue(geo.currentLongitude);*/
    }

    void goFun(View view)
    {
        Intent goIntent = new Intent(MainActivity.this,EmergencyActivity.class);
        startActivity(goIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
          //  super.onBackPressed();
            if (exit)
            {
                finish();
            }
            else
                {
                    Toast.makeText(this, "Press Back again to Exit.",
                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 2 * 1000);
                }
        }
    }

}

