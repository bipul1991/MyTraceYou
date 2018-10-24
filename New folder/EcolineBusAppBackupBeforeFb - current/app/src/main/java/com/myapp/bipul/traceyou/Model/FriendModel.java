package com.myapp.bipul.traceyou.Model;

/**
 * Created by Bipul on 03-Apr-18.
 */

public class FriendModel {

    private String frId;
    private String frName;
    private String frPhone;
    private String frBlock;
    private String frImg;
    private String frAddress;
    private String lat;
    private String lng;
    private String time;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    private String fcmId;

    public String getFrId() {
        return frId;
    }

    public void setFrId(String frId) {
        this.frId = frId;
    }

    public String getFrName() {
        return frName;
    }

    public void setFrName(String frName) {
        this.frName = frName;
    }

    public String getFrPhone() {
        return frPhone;
    }

    public void setFrPhone(String frPhone) {
        this.frPhone = frPhone;
    }

    public String getFrBlock() {
        return frBlock;
    }

    public void setFrBlock(String frBlock) {
        this.frBlock = frBlock;
    }

    public String getFrImg() {
        return frImg;
    }

    public void setFrImg(String frImg) {
        this.frImg = frImg;
    }

    public String getFrAddress() {
        return frAddress;
    }

    public void setFrAddress(String frAddress) {
        this.frAddress = frAddress;
    }


}
