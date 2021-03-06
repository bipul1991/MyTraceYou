package com.myapp.bipul.traceyou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.bipul.traceyou.Database.LocalDb;
import com.myapp.bipul.traceyou.Fcm_Database_Helper.UserInfoModel;
import com.myapp.bipul.traceyou.Helper.AllApis;
import com.myapp.bipul.traceyou.Helper.Help;
import com.myapp.bipul.traceyou.Model.FriendModel;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bipul on 03-Apr-18.
 */

public class FriendBlockFrg extends Fragment {

    RecyclerView frg_rycl;
    TextView norecordTxt;
    List<FriendModel> listModel;

    ArrayList<UserInfoModel> userInfoList = new ArrayList<>();
    Intent globintent;
    ItemAdaptr adapter;


    EditText mobileEdt;

    String myNumber="";
    String myToken="",myName="",myImgLink="";

    String fcmUrl="";
    SharedPreferences sharedPreferences;

    Help help;
    DatabaseReference databaseReference;
    DatabaseReference mainDatabaseRef;
    LocalDb localDb;
    public FriendBlockFrg()
    {

    }
    @SuppressLint("ValidFragment")

    public FriendBlockFrg(List<FriendModel> listModel)
    {
        this.listModel = listModel;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.frg_container, container, false);

        AllApis allApis = new AllApis();
        fcmUrl =allApis.fcmUrl;

        help = new Help(getActivity());

        frg_rycl =(RecyclerView)view.findViewById(R.id.frg_rycl);
        norecordTxt =(TextView)view.findViewById(R.id.norecordTxt);

        localDb = new LocalDb(getActivity());
        //  listModel = new ArrayList<>();

        frg_rycl.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        frg_rycl.setLayoutManager(mLayoutManager);
        frg_rycl.setItemAnimator(new DefaultItemAnimator());

        mainDatabaseRef = FirebaseDatabase.getInstance().getReference(myNumber);

        sharedPreferences =getActivity().getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        myNumber = sharedPreferences.getString("number","");
        myToken = sharedPreferences.getString("fcmId","");
        myName = sharedPreferences.getString("name","");
        myImgLink = sharedPreferences.getString("imgLink","");



        //   makeListView();
        if(listModel.size()<1)
        {
            norecordTxt.setVisibility(View.VISIBLE);
        }
        else
        {
            norecordTxt.setVisibility(View.GONE);
            adapter = new ItemAdaptr(getActivity());
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
            final String tag= "rqLocation";
            final String frdToken= listModel.get(position).getFcmId();
            //   final String imgLink= listModel.get(position).getFrImg();
          /*  final double lat= Double.parseDouble(listModel.get(position).getLat());
            final double lng= Double.parseDouble(listModel.get(position).getLng());
            final LatLng myLatLng= new LatLng(lat,lng);
*/

            double lat=Double.parseDouble(listModel.get(position).getLat());
            if(lat>0)
            {
                holder.adrsTxt.setText(help.getAddress(new LatLng(Double.parseDouble(listModel.get(position).getLat()),Double.parseDouble(listModel.get(position).getLng()))));
            }
            else
            {
                holder.adrsTxt.setText("no address found");
            }
            holder.locBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        databaseReference = FirebaseDatabase.getInstance().getReference(myNumber).child("friends")
                                .child(listModel.get(position).getFrPhone());
                        databaseReference.child("block").setValue("no");
                        listModel.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                }
            });

            //   holder.adrsTxt.setText(listModel.get(position).getFrAddress());


            // holder.colorLay.setBackgroundColor(Integer.parseInt(listModel.get(position).getColorCode()));
            // Log.d("colorCodeCheck ",listModel.get(position).getColorCode());


            holder.mainLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Expend Layout
                    holder.expendableLay.toggle();
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

                msgBtn.setVisibility(View.GONE);
                locBtn.setText("unbolck");

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

}


