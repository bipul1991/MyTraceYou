package com.myapp.bipul.traceyou;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.LocationListener;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearByPlaceActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    Button hospitalBtn;
    int PROXMITY_RADIUS = 2000;
    double latitude, longitude;

    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;

    private Button sViewBtn,nViewBtn;

    AdView adView;
    AdRequest adRequest;

    public static final int REQUEST_LOCATION_CODE = 99;

    LinearLayout restaurantlay,hospitalLay,policeLay,atmLay;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_place);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

/*
        adView = (AdView)findViewById(R.id.adView);
      // MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
       MobileAds.initialize(this, "ca-app-pub-1806296421186622~2732604891");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/

        nViewBtn = (Button)findViewById(R.id.nViewBtn);
        sViewBtn = (Button)findViewById(R.id.sViewBtn);

        sViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        nViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // onMapReady();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

     //  hospitalBtn = (Button) findViewById(R.id.hospitalBtn);

        atmLay = (LinearLayout)findViewById(R.id.atmLay);
        hospitalLay = (LinearLayout)findViewById(R.id.hospitalLay);
        restaurantlay = (LinearLayout)findViewById(R.id.restaurantlay);
        policeLay = (LinearLayout)findViewById(R.id.policeLay);

        atmLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NearByPlaceActivity.this,"Nearby ATM...",Toast.LENGTH_SHORT).show();
                showPlaces("atm");
            }
        });

        restaurantlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NearByPlaceActivity.this,"Nearby Restaurant...",Toast.LENGTH_SHORT).show();
                showPlaces("restaurant");
            }
        });

        hospitalLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(NearByPlaceActivity.this,"Nearby Hospital...",Toast.LENGTH_SHORT).show();
                showPlaces("hospital");

            }
        });

        policeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NearByPlaceActivity.this,"Nearby Police Station...",Toast.LENGTH_SHORT).show();
                showPlaces("police");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    // permission granted
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                }

                else // permission denied
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }

            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }

    public void showPlaces(String place) {
        mMap.clear();
        String url = getUrl(latitude, longitude, place);

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude,longitude));
        markerOptions.title("my position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentLocationMarker = mMap.addMarker(markerOptions);

        GetNearByPlacesData getNearByPlacesData = new GetNearByPlacesData();
        getNearByPlacesData.execute(dataTransfer);
       // Toast.makeText(NearByPlaceActivity.this, place+"...", Toast.LENGTH_LONG).show();

    }

    private String getUrl(double latitude, double longitude, String nearByPlace) {

      //  StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearByPlace);
     //   googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&keyword="+nearByPlace);
        googlePlaceUrl.append("&key=AIzaSyB2vvtklkbJuUly672xOA00Bs8_GRnoHVY");


        return googlePlaceUrl.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
       // mMap.getUiSettings().setZoomControlsEnabled(true);
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(SomePos));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target)
                .zoom(17)
                .bearing(40)
                .tilt(45)
                .build()));

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        LatLng lng = new LatLng(latitude,longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lng));

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

    @Override
    public void onLocationChanged(Location location) {

        Log.d("changedLocNearBy: ", "startLocationUpdatesAfterResume called");

        lastLocation = location;

        if(currentLocationMarker !=null)
        {
            currentLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        latitude = location.getLatitude(); longitude=location.getLongitude();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("my position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(11));

        if(client !=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

      /*  locationRequest = LocationRequest.create();
        locationRequest.setSmallestDisplacement(5);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(6000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(50);
        // locationRequest.setInterval(1000*60*30);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==(PackageManager.PERMISSION_GRANTED)) {

            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("changedLocNearBy: ", "onConnectionSuspended called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("changedLocNearBy: ", "onConnectionFailed called");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    public void fun_click(View view)
    {
        if(view.getId()== R.id.restaurantlay)
        {
            Toast.makeText(NearByPlaceActivity.this,"restaurant",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
