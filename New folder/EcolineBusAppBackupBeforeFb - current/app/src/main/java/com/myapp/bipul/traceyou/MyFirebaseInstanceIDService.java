package com.myapp.bipul.traceyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Bipul on 12-Feb-18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{

    SharedPreferences sharedPreferences;
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("tokenCheck", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
      //  sendRegistrationToServer(refreshedToken);

        sharedPreferences = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("fcmId", refreshedToken);
        edit.commit();

    }

}
