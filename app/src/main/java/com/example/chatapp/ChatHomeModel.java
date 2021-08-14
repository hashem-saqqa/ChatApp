package com.example.chatapp;

import android.net.Uri;

public class ChatHomeModel {
    private String profileImage;
    private String userName;
    private String time;
    private String lastMsg;

    public ChatHomeModel(String profileImage, String userName, String time, String lastMsg) {
        this.profileImage = profileImage;
        this.userName = userName;
        this.time = time;
        this.lastMsg = lastMsg;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
