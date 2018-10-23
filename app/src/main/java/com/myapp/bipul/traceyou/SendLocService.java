package com.myapp.bipul.traceyou;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.myapp.bipul.traceyou.Helper.AllApis;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimerTask;

import static com.myapp.bipul.traceyou.MyFirebaseMessagingService.Fcm_Token;

/**
 * Created by Bipul on 10-May-18.
 */

public class SendLocService extends Service {



   public static final String ACTION_START = "com.example.mobot.ecoline.SendLocService.ACTION_START";
    private TimerTask doAsynchronousTask;
    final Handler handler = new Handler();
    private CountDownTimer timer;

    ServiceBroadCat receiver;

    private static final String ACTION_IN_LOC_SER = "com.example.mobot.ecoline.service";

   /*public SendLocService(String token,String msg)
   {
        this.token=token;
        this.msg=msg;
   }

   public SendLocService()
   {

   }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_IN_LOC_SER);
        this.receiver = new ServiceBroadCat();
        this.registerReceiver(this.receiver, filter);



        String token="";
        String msg="";
        String action = intent.getAction();

       /* if(intent.getExtras() !=null)
        {
            token=intent.getExtras().getString("token","");
            msg=intent.getExtras().getString("msg","");
            Log.d("serRes"," "+token+"\n\n"+msg);
            sendLocation( token,msg);
        }
        else
            {
                Log.d("serRes"," emptymmmmmm");
            }*/
    //    sendLocation( token,msg);
    //    runtimer();
       /* if(!Fcm_Token.isEmpty()) {
            runtimer();
        }*/

        return Service.START_STICKY;
    }

    public class ServiceBroadCat extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("threadChk", "live");
        }
    }

    public class MessagingService extends FirebaseMessagingService
    {

    }


private void runtimer()
{

    timer = new CountDownTimer(5000, 2000) {

        @Override
        public void onTick(long millisUntilFinished) {
            if(!Fcm_Token.isEmpty()) {
               // Log.d("threadChk", "live");

                Fcm_Token="";
            }
        }

        @Override
        public void onFinish() {
            try{
                runtimer();
            }catch(Exception e){
                Log.e("Error", "Error: " + e.toString());
            }
        }
    }.start();

}




    public  void  sendLocation( String token,String msg)
    {
        AllApis allApis = new AllApis();
        String url=allApis.fcmUrl;

        new SendNotificationAsync().execute(url,token,msg);
    }

    class SendNotificationAsync extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... parms) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String token=parms[1];
            String msg = parms[2];
            String urls= parms[0];

            try {
                URL url = new URL(urls);
                connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()

                        .appendQueryParameter("token",token)
                        .appendQueryParameter("message",msg);




                String query = builder.build().getQuery();
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                String line = "";
                StringBuffer buffer = new StringBuffer();

                while ((line=reader.readLine())!=null)
                {
                    buffer.append(line);
                }


                return buffer.toString();





            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d("postExc",".."+s);
        }
    }
}
