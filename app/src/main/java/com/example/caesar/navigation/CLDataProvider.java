package com.example.caesar.navigation;

import android.widget.ImageView;

/**
 * Created by SABA on 7/29/2015.
 */
public class CLDataProvider {

    public ImageView Picture;
    public String gcmID;
    public String androidID;
    public String roomhash;
    public String lastmsg = "lol what";
    public ChatAdapter chat;


    public CLDataProvider(String gcmID) {
        super();
        this.gcmID = gcmID;
        this.androidID = androidID;
        this.roomhash = roomhash;

    }

    public CLDataProvider(String android_id, String gcm_id, String newMsg, ChatAdapter chat) {
        this.lastmsg = newMsg;
    }



}