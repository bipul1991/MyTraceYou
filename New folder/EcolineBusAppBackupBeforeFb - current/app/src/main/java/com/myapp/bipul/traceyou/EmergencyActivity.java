package com.myapp.bipul.traceyou;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.bipul.traceyou.Database.LocalDb;
import com.myapp.bipul.traceyou.Fcm_Database_Helper.UserFriendModel;
import com.myapp.bipul.traceyou.Model.FriendModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmergencyActivity extends AppCompatActivity {

    LocalDb localDb;
    List<FriendModel> listModel;
    List<FriendModel> listModelEmr;


    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
       // setContentView(R.layout.frg_container);

        init();

        localDb = new LocalDb(EmergencyActivity.this);
        listModel = new ArrayList<>();
        listModelEmr = new ArrayList<>();
        Cursor check = localDb.getAllExistNumber();
        if(check.moveToFirst())
        {
            FriendModel friendModel;
            do
            {
                friendModel = new FriendModel();
                friendModel.setFrId(check.getString(0));
                friendModel.setFrPhone(check.getString(1));
                friendModel.setFrName(check.getString(2));
                friendModel.setFrBlock(check.getString(3));

              //  Log.d("skuCheck",".."+check.getString(5));
                if(check.getString(4).equalsIgnoreCase("yes"))
                {
                    listModelEmr.add(friendModel);
                }

                else
                listModel.add(friendModel);

            }
            while (check.moveToNext());
            check.close();
            setupViewPager(viewPager);
    }

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

    void  init()
    {
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        {
           /* Log.d("ListModelTes",".."+listModel);
            Log.d("ListModelTes",".."+blockListModel);*/
            adapter.addFrag(new EmeargencyFrg(listModelEmr,"egcy"), "Emergency contact");
            adapter.addFrag(new EmeargencyFrg(listModel,"all"), "Your friends");

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




}
