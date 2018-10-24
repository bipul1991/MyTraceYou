package com.myapp.bipul.traceyou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.bipul.traceyou.Database.LocalDb;
import com.myapp.bipul.traceyou.Fcm_Database_Helper.UserFriendModel;
import com.myapp.bipul.traceyou.Model.FriendModel;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bipul on 03-Apr-18.
 */

public class FriendReqFrg extends Fragment {
    RecyclerView frg_rycl;
    TextView norecordTxt;
    List<FriendModel> listModel;


    Intent globintent;
    DatabaseReference databaseReference;
    DatabaseReference mainDatabaseRef;
    SharedPreferences sharedPreferences;
    String mynumber="";
    String myName="";

    ItemAdaptr adapter;

    public FriendReqFrg()
    {

    }
    @SuppressLint("ValidFragment")

    public FriendReqFrg(String baseUrl)
    {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.frg_container, container, false);

        frg_rycl =(RecyclerView)view.findViewById(R.id.frg_rycl);
        norecordTxt =(TextView)view.findViewById(R.id.norecordTxt);


        sharedPreferences =getActivity(). getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        mynumber = sharedPreferences.getString("number","");


        listModel = new ArrayList<>();

        LocalDb localDb = new LocalDb(getActivity());
        Cursor check = localDb.getAllReq();
        if (check.moveToFirst())
        {
            FriendModel friendModel;
            norecordTxt.setVisibility(View.GONE);
            do
            {
                friendModel = new FriendModel();
                friendModel.setFrId(check.getString(0));
                friendModel.setFrPhone(check.getString(1));
                friendModel.setFrName(check.getString(2));
                friendModel.setFrImg(check.getString(3));
                //  friendModel.setColorCode(check.getString(5));
                friendModel.setFrAddress(check.getString(4));
                friendModel.setFrBlock(check.getString(5));


                Log.d("skuCheck",".."+check.getString(5));

                listModel.add(friendModel);

            }

            while (check.moveToNext());

            frg_rycl.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            frg_rycl.setLayoutManager(mLayoutManager);
            frg_rycl.setItemAnimator(new DefaultItemAnimator());
            adapter = new FriendReqFrg.ItemAdaptr(getActivity());
            frg_rycl.setAdapter(adapter);

            check.close();
        }

        else
        {
           norecordTxt.setVisibility(View.VISIBLE);
           /* frg_rycl.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            frg_rycl.setLayoutManager(mLayoutManager);
            frg_rycl.setItemAnimator(new DefaultItemAnimator());
            adapter = new FriendReqFrg.ItemAdaptr(getActivity());
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
            holder.adrsTxt.setText(listModel.get(position).getFrAddress());

            // holder.colorLay.setBackgroundColor(Integer.parseInt(listModel.get(position).getColorCode()));
            // Log.d("colorCodeCheck ",listModel.get(position).getColorCode());




            holder.addFrnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFriend(listModel.get(position).getFrPhone(),position);

                }
            });

            holder.cancelFrnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LocalDb localDb = new LocalDb(getActivity());
                    localDb.deleteFriendReq(listModel.get(position).getFrId());
                    listModel.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });

            holder.mainLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Expend Layout
                    holder.expendableLay.toggle();
                }
            });

            holder.mainLay.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("Alert")
                            .setMessage("We are working to add more features on it ")
                            /*.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            Databaase1 databaase1 = new Databaase1(ItemListActivity.this);
                                            databaase1.deleteItem(listModel.get(position).getItemId());
                                            globintent = new Intent(ItemListActivity.this, ItemListActivity.class);
                                            startActivity(globintent);
                                            finish();

                                        }
                                    }

                            )*/


                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return false;
                }
            });




        }


        @Override
        public int getItemCount() {

            return listModel.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            LinearLayout mainLay;
            TextView nameTxt,phoneTxt,adrsTxt;
            CircleImageView profileImg;
            Button addFrnd,cancelFrnd;
            ExpandableRelativeLayout expendableLay;


            public ViewHolder(View itemView) {
                super(itemView);

                nameTxt = (TextView) itemView.findViewById(R.id.nameTxt);
                phoneTxt = (TextView) itemView.findViewById(R.id.phoneTxt);
                adrsTxt = (TextView) itemView.findViewById(R.id.adrsTxt);
                profileImg = (CircleImageView) itemView.findViewById(R.id.profileImg);
                mainLay = (LinearLayout) itemView.findViewById(R.id.mainLay);
                addFrnd =(Button)itemView.findViewById(R.id.msgBtn);
                cancelFrnd =(Button)itemView.findViewById(R.id.locBtn);
                expendableLay=(ExpandableRelativeLayout)itemView.findViewById(R.id.expandableRL);
                addFrnd.setText("Accept");
                cancelFrnd.setText("Cancel");


            }
        }

        private void addFriend(final String mobileId, final int position)
        {

            //  databaseReference = FirebaseDatabase.getInstance().getReference().child(mobileId);

            databaseReference = FirebaseDatabase.getInstance().getReference(mobileId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(getActivity(), "ondata change", Toast.LENGTH_SHORT).show();

                    Log.d("mobileCheck", ".." + mobileId);




                    if (dataSnapshot.getValue() != null) {

                        String  userName = dataSnapshot.child("name").getValue().toString();
                        String userEmail =dataSnapshot.child("email").getValue().toString();
                        String mobile = mobileId;
                        String lat=dataSnapshot.child("lat").getValue().toString();
                        String lng=dataSnapshot.child("lng").getValue().toString();
                        String imgLink=dataSnapshot.child("imglink").getValue().toString();
                        String time=dataSnapshot.child("time").getValue().toString();
                        //  String block=dataSnapshot.child("block").getValue().toString();
                        String fcmId=dataSnapshot.child("fcmId").getValue().toString();
                        //  num = dataSnapshot.child("email").getValue().toString();
                        Log.d("emailTest", "...." + userEmail + "  name: " + userName);

                      /*  if (!localDb.getExistNumber(mobileId)) {
                            localDb.insertExist(mobileId, userName);
                        }*/
                        mainDatabaseRef =FirebaseDatabase.getInstance().getReference(mynumber);
                        UserFriendModel userInfoModel = new UserFriendModel(mobile,userName,userEmail,fcmId,lat,lng,imgLink,time);
                        mainDatabaseRef.child("friends").child(mobileId).setValue(userInfoModel);

                        LocalDb localDb = new LocalDb(getActivity());
                        localDb.deleteFriendReq(listModel.get(position).getFrId());
                        listModel.remove(position);
                        adapter.notifyDataSetChanged();

                        //    Log.d("localDb","...."+(localDb.getExistNumber(mobileId)));
                    }
                    // Log.d("contactCheck","...."+num);

                    //  databaseReference = FirebaseDatabase.getInstance().getReference("89061065700").child("friends").child("frnd");
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"on Cancelled",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public  void setValues()
    {

        //    listModel = new ArrayList<>();
        LocalDb localDb = new LocalDb(getActivity());
        Cursor check = localDb.getAllFrnd();



        listModel = new ArrayList<>();


        if (check.moveToFirst()) {

            do {
                FriendModel friendModel = new FriendModel();
                friendModel.setFrId(check.getString(0));
                friendModel.setFrPhone(check.getString(1));
                friendModel.setFrName(check.getString(2));
                friendModel.setFrImg(check.getString(3));
                friendModel.setFrBlock(check.getString(4));
                //  friendModel.setColorCode(check.getString(5));
                friendModel.setFrAddress(check.getString(5));


                Log.d("skuCheck",".."+check.getString(5));

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

