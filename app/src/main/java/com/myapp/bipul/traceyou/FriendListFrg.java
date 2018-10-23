package com.myapp.bipul.traceyou;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.bipul.traceyou.Helper.AllApis;
import com.myapp.bipul.traceyou.Database.LocalDb;
import com.myapp.bipul.traceyou.Helper.Help;
import com.myapp.bipul.traceyou.Model.FriendModel;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bipul on 03-Apr-18.
 */

public class FriendListFrg extends Fragment {

    RecyclerView frg_rycl;
    TextView norecordTxt;
    List<FriendModel> listModel;

    Intent globintent;
    ItemAdaptr adapter;


    EditText mobileEdt;

    String myNumber="";
    String myToken="",myName="",myImgLink="";

    String fcmUrl="";
    SharedPreferences sharedPreferences;
    Intent globIntent;


    DatabaseReference databaseReference;
    DatabaseReference mainDatabaseRef;
    LocalDb localDb;
    Geo geo = new Geo();
    Help help;

    ProgressDialog pdLoading;

    public FriendListFrg()
    {

    }
    @SuppressLint("ValidFragment")

    public FriendListFrg(List<FriendModel> listModel)
    {
        this.listModel = listModel;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.frg_container, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        myNumber = sp.getString("number","");

        AllApis allApis = new AllApis();
        fcmUrl =allApis.fcmUrl;

        frg_rycl =(RecyclerView)view.findViewById(R.id.frg_rycl);
        norecordTxt =(TextView)view.findViewById(R.id.norecordTxt);

        localDb = new LocalDb(getActivity());
        help = new Help(getActivity());
      //  listModel = new ArrayList<>();

        frg_rycl.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        frg_rycl.setLayoutManager(mLayoutManager);
        frg_rycl.setItemAnimator(new DefaultItemAnimator());

     //   mainDatabaseRef = FirebaseDatabase.getInstance().getReference(myNumber);

        sharedPreferences =getActivity().getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        myToken = sharedPreferences.getString("fcmId","");
        myName = sharedPreferences.getString("name","");
        myImgLink = sharedPreferences.getString("imgLink","");


     //   makeListView();
        if(listModel.size()<1)
        {
            norecordTxt.setVisibility(View.VISIBLE);
            norecordTxt.setText("no one present in TRACE YOU from your contact list please shear this app to your friends");
        }
        else
            {
                norecordTxt.setVisibility(View.GONE);
                adapter = new FriendListFrg.ItemAdaptr(getActivity());
                frg_rycl.setAdapter(adapter);
            }





        LocalDb localDb = new LocalDb(getActivity());
        Cursor check = localDb.getAllExistNumber();
        if (check.moveToFirst())
        {
            FriendModel friendModel;

           // norecordTxt.setVisibility(View.GONE);
            do
            {
               /* friendModel = new FriendModel();
                friendModel.setFrId(check.getString(0));
                friendModel.setFrPhone(check.getString(1));
                friendModel.setFrName(check.getString(2));
              *//*  friendModel.setFrImg(check.getString(3));
                friendModel.setFrBlock(check.getString(4));
               //friendModel.setColorCode(check.getString(5));
                friendModel.setFrAddress(check.getString(5));*//*

               Log.d("skuCheck",".."+check.getString(1));
               listModel.add(friendModel);*/

            }

            while (check.moveToNext());




            check.close();
        }

        else
        {
          //  norecordTxt.setVisibility(View.VISIBLE);
          /*  frg_rycl.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            frg_rycl.setLayoutManager(mLayoutManager);
            frg_rycl.setItemAnimator(new DefaultItemAnimator());
            adapter = new ItemAdaptr(getActivity());
            frg_rycl.setAdapter(adapter);*/
        }
        return view;
    }

    public class ItemAdaptr extends RecyclerView.Adapter<ItemAdaptr.ViewHolder> {

        Context context;
        // List<ItemModel> listModel;


        public ItemAdaptr(Context context) {
            this.context = context;
            // this.listModel= listModel;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend_list, parent, false);

            return new ViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.nameTxt.setText(listModel.get(position).getFrName());
            holder.phoneTxt.setText(listModel.get(position).getFrPhone());
double lat=Double.parseDouble(listModel.get(position).getLat());
if(lat>0)
{
    holder.adrsTxt.setText(help.getAddress(new LatLng(Double.parseDouble(listModel.get(position).getLat()),Double.parseDouble(listModel.get(position).getLng()))));
}
else
    {
        holder.adrsTxt.setText("no address found");
    }
          //  LatLng latLng1=new LatLng(Double.parseDouble(listModel.get(position).getLat()),Double.parseDouble(listModel.get(position).getLng()));
            final String tag= "rqLocation";
            final String frdToken= listModel.get(position).getFcmId();
         //   final String imgLink= listModel.get(position).getFrImg();



            holder.locBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    try {
                        Log.d("positionChk ",".."+myName);

                        String lat = String.valueOf(geo.currentLatitude);
                        String lng = String.valueOf(geo.currentLongitude);
                       JSONObject sendJson= makeJson(tag,"","",myName,myNumber,myToken,myImgLink,lat,lng);
                        Log.d("tokenChecki ","frnd: "+frdToken+" \n my: "+myToken);
/*
                       String frName=listModel.get(position).getFrName().toString();
                       String frNum=listModel.get(position).getFrPhone().toString();*/

                      // new SendNotificationAsync().execute(fcmUrl,frdToken,sendJson.toString());

                        Log.d("positionChk ",".."+position+"..tok.."+frdToken);
                       new SendNotificationAsync().execute(fcmUrl,frdToken,sendJson.toString()
                               ,listModel.get(position).getFrPhone().toString(),listModel.get(position).getFrName().toString());




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

         //   holder.adrsTxt.setText(listModel.get(position).getFrAddress());


            // holder.colorLay.setBackgroundColor(Integer.parseInt(listModel.get(position).getColorCode()));
           // Log.d("colorCodeCheck ",listModel.get(position).getColorCode());

            holder.mainLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.expendableLay.toggle();
                }
            });


            holder.msgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("Alert")
                            .setMessage("are you sure want to block this firend?? ")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            try {


                                                databaseReference = FirebaseDatabase.getInstance().getReference(myNumber).child("friends")
                                                        .child(listModel.get(position).getFrPhone());
                                                databaseReference.child("block").setValue("yes");
                                                listModel.remove(position);
                                                adapter.notifyDataSetChanged();
                                            }
                                            catch (Exception ex)
                                            {
                                                ex.printStackTrace();
                                            }

                                        }
                                    }

                            )
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        }
                                    }

                            )


                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();



        }
            });



        }


        @Override
        public int getItemCount() {
         // return 5;
         return listModel.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            LinearLayout mainLay;
            TextView nameTxt,phoneTxt,adrsTxt;
            CircleImageView profileImg;
            Button msgBtn,locBtn;
            ExpandableRelativeLayout expendableLay;

            public ViewHolder(View itemView) {
                super(itemView);

                nameTxt = (TextView) itemView.findViewById(R.id.nameTxt);
                phoneTxt = (TextView) itemView.findViewById(R.id.phoneTxt);
                adrsTxt = (TextView) itemView.findViewById(R.id.adrsTxt);
                profileImg = (CircleImageView) itemView.findViewById(R.id.profileImg);
                mainLay = (LinearLayout) itemView.findViewById(R.id.mainLay);
                msgBtn =(Button)itemView.findViewById(R.id.msgBtn);
                locBtn =(Button)itemView.findViewById(R.id.locBtn);
                expendableLay=(ExpandableRelativeLayout)itemView.findViewById(R.id.expandableRL);



            }
        }
    }

    public  void setValues()
    {

        //    listModel = new ArrayList<>();
        LocalDb localDb = new LocalDb(getActivity());
        Cursor check = localDb.getAllExistNumber();



        listModel = new ArrayList<>();


        if (check.moveToFirst()) {

            do {
                FriendModel friendModel = new FriendModel();
                friendModel.setFrId(check.getString(0));
                friendModel.setFrPhone(check.getString(1));
                friendModel.setFrName(check.getString(2));
               /* friendModel.setFrImg(check.getString(3));
                friendModel.setFrBlock(check.getString(4));
                //  friendModel.setColorCode(check.getString(5));
                friendModel.setFrAddress(check.getString(5));


                Log.d("skuCheck",".."+check.getString(5));*/

                listModel.add(friendModel);
            }

            while (check.moveToNext());


        }
        //  adapter.notifyDataSetChanged();
        if(listModel.size()<1) {
            norecordTxt.setVisibility(View.VISIBLE);
        }
        else {
            norecordTxt.setVisibility(View.GONE);
        }
        if(adapter !=null) {
            adapter.notifyDataSetChanged();
        }
        check.close();
    }



    public JSONObject makeJson( String tage,String titel, String message,String name,String number, String myToken,String imgLink, String lat,String lng) throws JSONException
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


    class SendNotificationAsync extends AsyncTask<String,String,String>
    {

        String fnumber="";
        String fName="";

        @Override
        protected void onPreExecute() {
            pdLoading = new ProgressDialog(getActivity());
            pdLoading.setMessage("\tSearching your friend location...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... parms) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String token=parms[1];
            String msg = parms[2];
            String urls= parms[0];
            fnumber =parms[3];
            fName =parms[4];

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
            finally {
                pdLoading.dismiss();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pdLoading.dismiss();
            Log.d("asyncRs","...."+s);
            globIntent = new Intent(getActivity(),FriendDirectionActivity.class);

                     /*   globintent.putExtra("fnumber",fnumber);
                        globintent.putExtra("fname",fName);*/

            sharedPreferences =getActivity().getSharedPreferences("traceYou", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("fnumber",fnumber);
            edit.putString("fname",fName);
            edit.commit();

            getActivity().startActivity(globIntent);
           // getActivity().finish();


        }
    }

}
