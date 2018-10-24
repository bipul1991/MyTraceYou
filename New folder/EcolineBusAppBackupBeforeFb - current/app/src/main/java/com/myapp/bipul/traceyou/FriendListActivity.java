package com.myapp.bipul.traceyou;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.myapp.bipul.traceyou.Database.LocalDb;
import com.myapp.bipul.traceyou.Fcm_Database_Helper.UserFriendModel;
import com.myapp.bipul.traceyou.Fcm_Database_Helper.UserInfoModel;
import com.myapp.bipul.traceyou.Helper.NetworkUtlities;
import com.myapp.bipul.traceyou.Model.FriendModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    int position = 0;
    boolean flag=false;

    String url,name,logo;
    int checker=0;
    String cCode="";
    String mynumber="";
    int svCount=0;

    Intent globIntent;

    DatabaseReference databaseReference;
    DatabaseReference mainDatabaseRef;
    SharedPreferences sharedPreferences;

    LocalDb localDb;
    List<FriendModel> listModel;
    List<FriendModel> blockListModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        localDb = new LocalDb(FriendListActivity.this);

        SharedPreferences sp = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        String nameCheck = sp.getString("name","");
        cCode = sp.getString("countryCode","");

        mynumber = sp.getString("number","");
        svCount = sp.getInt("count",1);

        Log.d("countryCode", ".." + cCode);

        name=nameCheck;
        mainDatabaseRef = FirebaseDatabase.getInstance().getReference();


        listModel = new ArrayList<FriendModel>();
        blockListModel = new ArrayList<FriendModel>();

        if(NetworkUtlities.isConnected(FriendListActivity.this))
        {
            makeListView();
           // startTheard();

        }
        else
        {
         Toast.makeText(FriendListActivity.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
         return;
        }


        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {

        /*  //  position = b.getInt("pos");
         //   position  = Integer.parseInt(b.getString("pos"));
            String pos=b.getString("pos","");*/
           /* name=b.getString("name");
            logo=b.getString("logo");
            url=b.getString("url");

            flag=true;
            Log.i("nullactivity","true "+name+ "  "+b);
            Log.i("alldataReg","true "+name+ "  "+logo+" "+url);
            checker=1;*/

        } else {
            flag = false;
            Log.i("nullactivity",""+position+" false");
            checker=0;
        }

       // setupViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        {
           /* Log.d("ListModelTes",".."+listModel);
            Log.d("ListModelTes",".."+blockListModel);*/

            adapter.addFrag(new FriendListFrg(listModel), "Friends");
            adapter.addFrag(new FriendReqFrg(), "New Request");
            adapter.addFrag(new FriendBlockFrg(blockListModel), "Blocked");

        }


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);
        adapter.notifyDataSetChanged();
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }


  /*  public void searchInFcm(final String mobileId, final String name)
    {
String number="";
        //  databaseReference = FirebaseDatabase.getInstance().getReference().child(mobileId);
        if(!mobileId.contains(cCode))
        {
            number = cCode+mobileId;

        }
        else
            {
                number = mobileId;
            }

        databaseReference = FirebaseDatabase.getInstance().getReference().child(number);

        final String finalNumber = number;
      //  Log.d("countryNum",".."+finalNumber);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {




                    if (dataSnapshot.getValue() != null) {

                        Toast.makeText(FriendListActivity.this, "ondata change", Toast.LENGTH_SHORT).show();

                        Log.d("datacheck11", ".." + dataSnapshot.getValue());

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userEmail =dataSnapshot.child("email").getValue().toString();
                        String mobile =dataSnapshot.child("mobile").getValue().toString();
                        String lat=dataSnapshot.child("lat").getValue().toString();
                        String lng=dataSnapshot.child("lng").getValue().toString();
                        String imgLink=dataSnapshot.child("imglink").getValue().toString();
                        String time=dataSnapshot.child("time").getValue().toString();
                      //  String block=dataSnapshot.child("block").getValue().toString();
                        String fcmId=dataSnapshot.child("fcmId").getValue().toString();
                        //  num = dataSnapshot.child("email").getValue().toString();
                        Log.d("emailTest", "...." + userEmail + "  name: " + userName);

                        boolean b2= localDb.getExistNumber(mobile);

                        if (!localDb.getExistNumber(mobile)) {
                            localDb.insertExist(mobile, name);

                        }
                        boolean b1= localDb.getExistNumber(mobile);
                        UserFriendModel userInfoModel = new UserFriendModel(mobile,name,userEmail,fcmId,lat,lng,imgLink,time);
                      //  mainDatabaseRef.child("friends").child(finalNumber).setValue(userInfoModel);
                        mainDatabaseRef.child(mynumber).child("friends").child(mobile).setValue( userInfoModel);

                        //    Log.d("localDb","...."+(localDb.getExistNumber(mobileId)));
                    }
                    // Log.d("contactCheck","...."+num);

                    //  databaseReference = FirebaseDatabase.getInstance().getReference("89061065700").child("friends").child("frnd");
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FriendListActivity.this,"on Cancelled",Toast.LENGTH_SHORT).show();
            }
        });
    }*/



    public void makeListView( )
    {

        databaseReference =FirebaseDatabase.getInstance().getReference().child(mynumber);
        //  databaseReference =FirebaseDatabase.getInstance().getReference().child("8906106579");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Log.d("CountSnap " ,"..."+dataSnapshot);


              /*  if(dataSnapshot.getKey().equalsIgnoreCase("frnd"))
                {
*/
              /*  Log.d("CountSnap22 " ,"..."+(dataSnapshot.getChildrenCount()));
                Log.d("CountSnap00 " ,"..."+(dataSnapshot.getValue()));*/

                if(dataSnapshot.getChildrenCount()>0)
                {
                    //norecordTxt.setVisibility(View.GONE);
                }

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    // String name  =ds.child("friends").child("frnd");
                    try {

                        UserInfoModel userInfoModel = ds.getValue(UserInfoModel.class);

                        FriendModel friendModel = new FriendModel();
                        //  friendModel.setFrId(check.getString(0));
                        friendModel.setFrPhone(userInfoModel.mobile);
                        friendModel.setFrName(userInfoModel.name);
                        friendModel.setFrImg(userInfoModel.imglink);
                        friendModel.setFcmId(userInfoModel.fcmId);


                        friendModel.setLat(userInfoModel.lat);
                        //  friendModel.setColorCode(check.getString(5));
                        friendModel.setLng(userInfoModel.lng);
                        friendModel.setFrBlock(userInfoModel.block);

                        if(userInfoModel.block==null)
                        {
                            listModel.add(friendModel);
                        }

                       else if ((userInfoModel.block).equalsIgnoreCase("no")) {
                            listModel.add(friendModel);
                        }
                        else
                            {
                            blockListModel.add(friendModel);
                        }
                        //  friendModel.setFrBlock(userInfoModel.block);


                       // Log.d("CountSnap 33", "..." + userInfoModel.block);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                }

                setupViewPager(viewPager);
                //   }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

  /* private void startTheard() //___________________________________// Access Contact List for search friend in FCM
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver cr =getContentResolver();
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);

                int  count = cursor.getCount();

                while (cursor.moveToNext()) {


                    try{
                       Thread.sleep(1000);
                        if(count>svCount){
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (phones.moveToNext()) {
                                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                       *//* HashMap<String,String> map=new HashMap<String,String>();
                        map.put("name", name);
                        map.put("number", phoneNumber);*//*
                                //  Log.d("contactCheck","...."+name+"  Num:.."+phoneNumber);

                                //  contactData.add(map);

                                searchInFcm(phoneNumber, name);
                            }
                            phones.close();

                        }

                            sharedPreferences = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putInt("count", count);
                            edit.commit();

                        }
                        //   Log.d("contactCheck",".."+contactData);

                    }
                    catch(Exception e){}
                }
                //    Log.d("contactCheck","...."+contactData);

            }
        }).start();
    }*/


    @Override
    public void onBackPressed() {
       /* globIntent = new Intent(FriendListActivity.this,MainActivity.class);
        startActivity(globIntent);*/
        finish();
    }
}



 /* sharedPreferences = getSharedPreferences("traceYou", Context.MODE_PRIVATE);
          SharedPreferences.Editor edit = sharedPreferences.edit();
          edit.putString("number", pNumber);
          edit.putString("countryCode", countryCode);
          edit.commit();*/