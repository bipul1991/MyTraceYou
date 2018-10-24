package com.myapp.bipul.traceyou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.myapp.bipul.traceyou.Database.LocalDb;
import com.myapp.bipul.traceyou.Model.FriendModel;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Bipul on 03-Jul-18.
 */

public class EmeargencyFrg  extends Fragment
{
    List<FriendModel> listModel;
    List<FriendModel> emgcyList;
    String tag="";

    RecyclerView frg_rycl;
    TextView norecordTxt;
    ListAllAdapter allListAdptr;
    ListEgcyAdptr listEgcyAdptr;

    LocalDb localDb;

    EmeargencyFrg()
    {

    }
    @SuppressLint("ValidFragment")
    EmeargencyFrg(List<FriendModel> listModel,String tag)
    {
        this.listModel = listModel;
       // this.emgcyList = emgcyList;
        this.tag=tag;
        
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.frg_container, container, false);

        frg_rycl =(RecyclerView)view.findViewById(R.id.frg_rycl);
        norecordTxt =(TextView)view.findViewById(R.id.norecordTxt);
        localDb = new LocalDb(getActivity());

        if(tag.equalsIgnoreCase("all"))
        {
            if (listModel.size() < 1) {
                norecordTxt.setVisibility(View.VISIBLE);
                norecordTxt.setText("You don't have any friend.\nplease shear this app with your friends for adding in your friend list");
            } else {
                norecordTxt.setVisibility(View.GONE);
                allListAdptr = new ListAllAdapter(getActivity());
                frg_rycl.setAdapter(allListAdptr);
            }
        }

        else if(tag.equalsIgnoreCase("egcy"))
        {
            if (listModel.size() < 1) {
                norecordTxt.setVisibility(View.VISIBLE);
            } else {
                norecordTxt.setVisibility(View.GONE);
                listEgcyAdptr = new ListEgcyAdptr(getActivity());
                frg_rycl.setAdapter(allListAdptr);
            }
        }
        return view;
    }


    public class ListAllAdapter extends RecyclerView.Adapter<ListAllAdapter.ViewHolder>  // All friends List adapter
    {

        Context context;
        String tag;
        // List<ItemModel> listModel;


        public ListAllAdapter(Context context) {
            this.context = context;
         
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

            holder.addEmergency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    localDb.updateEmergency(listModel.get(position).getFrId(),"make");
                    listModel.remove(position);
                    allListAdptr.notifyItemChanged(position);

                }
            });
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

            return listModel.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            LinearLayout mainLay;
            TextView nameTxt,phoneTxt;
            CircleImageView profileImg;
            Button addEmergency,cancelFrnd;
            ExpandableRelativeLayout expendableLay;



            public ViewHolder(View itemView) {
                super(itemView);

                nameTxt = (TextView) itemView.findViewById(R.id.nameTxt);
                phoneTxt = (TextView) itemView.findViewById(R.id.phoneTxt);
                profileImg = (CircleImageView) itemView.findViewById(R.id.profileImg);
                mainLay = (LinearLayout) itemView.findViewById(R.id.mainLay);
                addEmergency =(Button)itemView.findViewById(R.id.msgBtn);
                cancelFrnd =(Button)itemView.findViewById(R.id.locBtn);
                expendableLay=(ExpandableRelativeLayout)itemView.findViewById(R.id.expandableRL);

                addEmergency.setText("Add to emergency");
               // cancelFrnd.setText("Cancel");


            }
        }


    }

    public class ListEgcyAdptr extends RecyclerView.Adapter<ListEgcyAdptr.ViewHolder>  // Eemergency List adapter
    {

        Context context;
        String tag;
        // List<ItemModel> listModel;


        public ListEgcyAdptr(Context context) {
            this.context = context;

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
            holder.emergencyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    localDb.updateEmergency(listModel.get(position).getFrId(),"no");
                    listModel.remove(position);
                    listEgcyAdptr.notifyItemChanged(position);

                }
            });
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

            return listModel.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            LinearLayout mainLay;
            TextView nameTxt,phoneTxt;
            CircleImageView profileImg;
            Button emergencyBtn,cancelFrnd;
            ExpandableRelativeLayout expendableLay;



            public ViewHolder(View itemView) {
                super(itemView);

                nameTxt = (TextView) itemView.findViewById(R.id.nameTxt);
                phoneTxt = (TextView) itemView.findViewById(R.id.phoneTxt);
                profileImg = (CircleImageView) itemView.findViewById(R.id.profileImg);
                mainLay = (LinearLayout) itemView.findViewById(R.id.mainLay);
                emergencyBtn =(Button)itemView.findViewById(R.id.msgBtn);
                cancelFrnd =(Button)itemView.findViewById(R.id.locBtn);
                expendableLay=(ExpandableRelativeLayout)itemView.findViewById(R.id.expandableRL);

                emergencyBtn.setText("remove");
                cancelFrnd.setVisibility(View.GONE);


            }
        }


    }

}
