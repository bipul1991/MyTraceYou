package com.myapp.bipul.traceyou;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.myapp.bipul.traceyou.Helper.Help;
import com.myapp.bipul.traceyou.Helper.NetworkUtlities;

import java.util.concurrent.TimeUnit;


public class OtpActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText mobileEdt,otpEdt;
    Button sendBtn,cnfBtn,cnacelBtn;
    RelativeLayout mobileLay,otpLay;

    String verifiation_code;
    String pNumber;
    String countryCode ="";
    SharedPreferences sharedPreferences;

    private static final int REQUEST_CODE_PERMISSION = 2;
    public static final int PERMS_CODE = 123;

    private LocationRequest mLocationRequest;
    static final Integer GPS_SETTINGS = 0x7;
    PendingResult<LocationSettingsResult> result;
    GoogleApiClient client;

    protected static final String TAG = "LocationOnOff";
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;


    Intent globIntent;
    Help help;
    ProgressDialog pdLoading;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_verify_otp);
     // setContentView(R.layout.activity_otp);

       /* Intent intent1 = new Intent(OtpActivity.this, Geo.class);
        startService(intent1);*/

        mobileEdt = (EditText)findViewById(R.id.mobileEdt);
        otpEdt = (EditText)findViewById(R.id.otpEdt);

        sendBtn =(Button)findViewById(R.id.sendBtn);
        cnfBtn =(Button)findViewById(R.id.cnfBtn);
        cnacelBtn =(Button)findViewById(R.id.cnacelBtn);

        mobileLay = (RelativeLayout)findViewById(R.id.mobileLay);
        otpLay = (RelativeLayout)findViewById(R.id.otpLay);
        help = new Help(OtpActivity.this);
        sharedPreferences = getSharedPreferences("traceYou", Context.MODE_PRIVATE);

      /*  if(!sharedPreferences.getString("number","").isEmpty())
        {
            globIntent = new Intent(OtpActivity.this,MainActivity.class);
            startActivity(globIntent);
            finish();
        }*/

      if(NetworkUtlities.isConnected(OtpActivity.this))
      {
         if(!help.statusCheck())
        displayLocationSettingsRequest(OtpActivity.this);
      }

      else
          {
              Toast.makeText(OtpActivity.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
                finish();
          }

        Intent intent1 = new Intent(OtpActivity.this, Geo.class);
        startService(intent1);

        firebaseAuth = FirebaseAuth.getInstance();

        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {



            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                pdLoading.dismiss();
                Toast.makeText(OtpActivity.this,"Please try after sometime "+e,Toast.LENGTH_SHORT).show();

                Log.d("otpError",".."+e);

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                verifiation_code = s;
                pdLoading.dismiss();
                Toast.makeText(OtpActivity.this,"code sent ",Toast.LENGTH_SHORT).show();

              mobileLay.setVisibility(View.GONE);
            otpLay.setVisibility(View.VISIBLE);
            }
        };

        sendBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if(mobileEdt.getText().toString().isEmpty())
                {
                    Toast.makeText(OtpActivity.this,"Please enter your mobile number ",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hasPermission()) {
                  /*  pdLoading = new ProgressDialog(OtpActivity.this);
                    pdLoading.setMessage("\tPlease Wait...");
                    pdLoading.setCancelable(true);
                    pdLoading.show();

                    Help help = new Help(OtpActivity.this);
                    Geo geo = new Geo();

                    pNumber = mobileEdt.getText().toString();
                    LatLng latLng = new LatLng(geo.currentLatitude, geo.currentLongitude);
                    //countryCode = help.getCountryCode(latLng);
                    countryCode = help.GetCountryZipCode();
                   // countryCode = "+91";
                    pNumber = countryCode + pNumber;

                    Log.d("numberChk", "..." + pNumber);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(pNumber, 60, TimeUnit.SECONDS, OtpActivity.this, mCallBack);*/

                    sendOtp();

                }
                else
                    {
                        requsetPerms();
                    }
            }

        });



        cnfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cnfCode = otpEdt.getText().toString();

                if(cnfCode.isEmpty())
                {
                    Toast.makeText(OtpActivity.this,"Please enter your OTP ",Toast.LENGTH_SHORT).show();
                    return;
                }

                pdLoading = new ProgressDialog(OtpActivity.this); pdLoading.setMessage("\tPlease Wait...");
                pdLoading.setCancelable(true);
                pdLoading.show();



                if(verifiation_code !=null)
                {
                    verifyCode(verifiation_code,cnfCode);
                }
            }
        });
    }

    public void singInWithPhone(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            pdLoading.dismiss();


                            sharedPreferences = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                           // edit.putString("number", pNumber);
                            edit.putString("countryCode", countryCode);
                            edit.commit();

                            globIntent = new Intent(OtpActivity.this,ProfileActivity.class);
                            globIntent.putExtra("number",pNumber);
                            startActivity(globIntent);
                            finish();
                        }
                        else
                            {
                                pdLoading.dismiss();
                                Toast.makeText(OtpActivity.this,"Sing in Error",Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }

    public void verifyCode(String vycode, String inputCode)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vycode,inputCode);
        singInWithPhone(credential);
    }


    public void requsetPerms() {

        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                 Manifest.permission.ACCESS_FINE_LOCATION,

                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_CONTACTS,



        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(permissions, PERMS_CODE);

        }

    }

    @SuppressLint("WrongConstant")
    public boolean hasPermission() {

        int res = 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            return  true;
        }

        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
               Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS
        };


        for (String perms : permissions) {

            res = checkCallingOrSelfPermission(perms);

            if (!(res == PackageManager.PERMISSION_GRANTED)) {

                return false;
            }
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean allowed = true;

        switch (requestCode) {

            case PERMS_CODE:

                for (int res : grantResults) {

                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }


                break;

            default:

                allowed = false;

                break;

        }


        if (allowed) {
            sendOtp();
        }
        else {
            requsetPerms();
        }
    }

    private void sendOtp()
    {
        pdLoading = new ProgressDialog(OtpActivity.this);
        pdLoading.setMessage("\tPlease Wait...");
        pdLoading.setCancelable(true);
        pdLoading.show();

        Help help = new Help(OtpActivity.this);
        Geo geo = new Geo();

        pNumber = mobileEdt.getText().toString();
        LatLng latLng = new LatLng(geo.currentLatitude, geo.currentLongitude);
        //countryCode = help.getCountryCode(latLng);
        countryCode = help.GetCountryZipCode();
        // countryCode = "+91";
        pNumber = countryCode + pNumber;

        Log.d("numberChk", "..." + pNumber);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(pNumber, 60, TimeUnit.SECONDS, OtpActivity.this, mCallBack);
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
                            status.startResolutionForResult(OtpActivity.this, REQUEST_LOCATION);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}

