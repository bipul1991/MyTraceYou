package com.myapp.bipul.traceyou.Fcm_Database_Helper;

/**
 * Created by Bipul on 27-Mar-18.
 */

public class UserFriendModel {
    public String mobile;
    public String name;
    public String email;
    public String fcmId;
    public String lat;
    public String lng;
    public String imglink;
   // public String block;
    public String time;



    public UserFriendModel(String mobile, String name, String email, String fcmId,String lat,String lng,String imglink,String time) {
        this.mobile = mobile;
        this.name = name;
        this.email=email;
        this.fcmId=fcmId;
        this.lat=lat;
        this.lng=lng;
        this.imglink=imglink;

        this.time=time;
    }

    public UserFriendModel() {
    }
}
