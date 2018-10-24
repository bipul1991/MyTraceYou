package com.myapp.bipul.traceyou.Helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.myapp.bipul.traceyou.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.List;
import java.util.Locale;

/**
 * Created by Bipul on 16-Apr-18.
 */

public class Help {

    Context context;
    final static int REQUEST_LOCATION = 199;


   public Help(Context context)
   {

        this.context = context;

    }

    public Help()
    {

    }

    public String getAddress(LatLng latLng)
    {
        String countryName="",state="",town="",locality="";

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses.size() > 0)
        {
           countryName=addresses.get(0).getCountryName();
           state=addresses.get(0).getAdminArea();
           town=addresses.get(0).getSubAdminArea();

           locality=addresses.get(0).getLocality();
        }
        return countryName+" "+state+" "+town+" "+locality;
    }

    public JSONObject makeJson(String tage, String titel, String message, String name, String number, String myToken, String imgLink, String lat,String lng) throws JSONException
    {


        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("tage",tage);
        jsonMessage.put("title",titel);
        jsonMessage.put("msg",message);
        jsonMessage.put("name",name);
        jsonMessage.put("number",number);
        jsonMessage.put("token",myToken);
        jsonMessage.put("imgLink",imgLink);
        jsonMessage.put("lat",lat);
        jsonMessage.put("lng",lng);

        return jsonMessage;
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

    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return "+"+CountryZipCode;
    }

    public boolean statusCheck() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return  false;
        }
        else
            return true;
    }

    public String getMrkAdrs(LatLng latLng)
    {
        String countryName="",state="",town="",locality="",subLoc="";

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses.size() > 0)
        {
            countryName=addresses.get(0).getCountryName();
            state=addresses.get(0).getAdminArea();
            town=addresses.get(0).getSubAdminArea();

            locality=addresses.get(0).getLocality();
            subLoc=addresses.get(0).getSubLocality();
        }
        return state+" "+town+" "+locality;
    }



}
