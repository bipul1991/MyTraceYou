package com.myapp.bipul.traceyou;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.myapp.bipul.traceyou.Helper.NetworkUtlities;

/**
 * Created by mobot on 15-11-2017.
 */

public class AddService extends Service {

    BroadcastClass broadcastClass;
    private InterstitialAd mInterstitialAd;

    int counter=0, locNumber=0;
    Thread mainThread;

    //SharedPreferences sp;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
      //  createInterstitial();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);

        this.broadcastClass = new BroadcastClass();
        this.registerReceiver(broadcastClass,filter);

      //  sp = getSharedPreferences("traceYou", Context.MODE_PRIVATE);


        return Service.START_STICKY;
    }


    public class BroadcastClass extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, final Intent intent) {

          //  if (intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED))
        //    Toast.makeText(AddService.this,"Action received", Toast.LENGTH_LONG).show();
            if (intent.getAction().equals(Intent.ACTION_USER_UNLOCKED))
            {

            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {

           /*   //  Toast.makeText(AddService.this,"Counter locked = "+counter, Toast.LENGTH_LONG).show();
                locNumber =sp.getInt("locCheck",0);

                if(locNumber ==10)
                {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.remove("locCheck");
                    edit.commit();
                }
                else
                    {
                        locNumber =  locNumber+1;
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putInt("locCheck", locNumber);
                        edit.commit();
                    }*/

                if(counter == 9)
                {
/*
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mInterstitialAd = new InterstitialAd(AddService.this);

                    // set the ad unit ID
                   // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
                    // mInterstitialAd.setAdUnitId("ca-app-pub-6440573835305156/2136897730");
                     mInterstitialAd.setAdUnitId("ca-app-pub-1078548156141474/4251897908");

                    adRequest = new AdRequest.Builder()
                            .build();

                    // Load ads into Interstitial Ads
                    mInterstitialAd.loadAd(adRequest);

                    mInterstitialAd.setAdListener(new AdListener()
                    {
                        public void onAdLoaded() {
                            showInterstitial();
                        }

                    });*/
                    counter =0;
                }
                else
                {
                    counter =counter+1;
                }

            }


        }



        private void showInterstitial() {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }



    }

    private void createInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd = new InterstitialAd(AddService.this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        // mInterstitialAd.setAdUnitId("ca-app-pub-1806296421186622/5954219825");
        // ca-app-pub-1806296421186622/5954219825
        adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

        });
    }

}
