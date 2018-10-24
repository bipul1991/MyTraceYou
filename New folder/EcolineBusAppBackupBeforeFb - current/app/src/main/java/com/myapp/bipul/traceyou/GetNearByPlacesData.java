package com.myapp.bipul.traceyou;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mobot on 14-10-2017.
 */

public class GetNearByPlacesData extends AsyncTask<Object, String, String>
{

    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        url =(String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        List<HashMap<String,String>> nearByPlaceLIst = null;

        Log.d("onpost ","."+s);

        DataParserNearByPlace dataParser = new DataParserNearByPlace();
        nearByPlaceLIst = dataParser.parse(s);
        showNearByPlaces(nearByPlaceLIst);

    }

    private void showNearByPlaces (List <HashMap<String,String>> nearByPlaceList)
    {
        for(int i=0;i<nearByPlaceList.size();i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlaces = nearByPlaceList.get(i);

            String placeName = googlePlaces.get("place_name");
            String vicinity = googlePlaces.get("vicinity");

            double lat = Double.parseDouble(googlePlaces.get("lat"));
            double lng = Double.parseDouble(googlePlaces.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName+": "+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }


}
