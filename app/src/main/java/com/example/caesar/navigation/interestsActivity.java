package com.example.caesar.navigation;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class interestsActivity extends Activity {

    //this is for the taglayout
    final String TAG = "testingProject";
    Button adding;
    EditText interest;
    String stuff;
    ImageView imgFavorite;
    int id = 1;
    List<String> groupedInterests = new ArrayList<>();

    //what you need to send interests
    String android_id;
    String regid;
    final JSONObject Updateparams = new JSONObject();
    final JSONObject Matchparams = new JSONObject();
    final Webb webb = Webb.create();

    //other

    List<String> roomList;
    public Socket mSocket;

    String roomnum;

    String otherId;
    Button chat;


    {
        try {
            mSocket = IO.socket("https://who-sarodh.c9users.io");
        }
        catch (URISyntaxException e){}
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            regid = extras.getString("token");
        }


        roomList = new ArrayList<String>();
        interest = (EditText) findViewById(R.id.interest);
        adding = (Button) findViewById(R.id.add);
        chat = (Button) findViewById(R.id.chat);

        //getting the android id
        android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        webb.setBaseUri("https://who-sarodh.c9users.io");


        mSocket.connect();
        //mSocket.on("Chatroom", myMessage);
        //mSocket.on("typing",onTyping);
        //mSocket.on("new picture",onPictureReceived);



        //calls updateinterests to add interests and begin chat
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UpdateInterests();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //button when clicked creates a new taglayout filled with the interests
        adding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == adding) {

                    // Read the edit text
                    stuff = interest.getText().toString().trim();

                    // Inflate the tag layout
                    LayoutInflater layoutInflater = getLayoutInflater();
                    final ViewGroup root = (ViewGroup) findViewById(R.id.tagLayout);

                    final View tagView = layoutInflater.inflate(R.layout.tag_layout, root, false);
                    root.addView(tagView);
                    // Get access to the subviews of Tag View
                    final TextView tagTextView = (TextView) tagView.findViewById(R.id.tagTextView);
                    imgFavorite = (ImageView) tagView.findViewById(R.id.imageView);

                    tagTextView.setText(stuff);
                    tagView.setTag(stuff);
                    groupedInterests.add(stuff);


                    // Log.i(TAG, String.valueOf(groupedInterests));


                    imgFavorite.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View b) {
                                    //remove the tag
                                    ((ViewGroup) tagView.getParent()).removeView(tagView);


                                    String stringToRemove = (String) tagView.getTag();
                                    groupedInterests.remove(stringToRemove);
                                    // Log.i(TAG, String.valueOf(groupedInterests));

                                }
                            });
                    Log.i(TAG, "first id given" + id);

                    id = id + 1;
                    interest.setText("");
                }
            }

        });


    }


    public void UpdateInterests() throws Exception {


        final JSONArray array = new JSONArray();
        final JSONObject location = new JSONObject();
        final JSONObject coordinates = new JSONObject();


        if (groupedInterests != null) {
            array.put(groupedInterests);
        }


        String country = "USA";
        String state = "NYC";
        String longitude = "no value stored";
        String latitude = "novalue stored";


        Updateparams.put("id", android_id);
        Updateparams.put("array", array);
        Updateparams.put("regid", regid);
        location.put("country", country);
        location.put("state", state);
        location.put("coordinates", coordinates);
        Updateparams.put("location", location);

        UpdateRequest(Updateparams);
    }


    private void MatchRequest(final JSONObject id) throws Exception {

        AsyncTask<Void, Void, String> params = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Log.i(TAG, "PARAMS FOR MATCH REQUEST" + id);
                    JSONObject response = webb
                            .post("/findmatch")
                            .body(id)
                            .ensureSuccess()
                            .readTimeout(4000)
                            .asJsonObject()
                            .getBody();
                    Log.i(TAG, "RESULT: " + response);
                    roomnum = response.getString("RoomHash");
                    otherId = response.getString("regid");
                    roomList.add(roomnum);
                    mSocket.emit("CreateRoom", roomnum, regid);
                    Intent intent2 = new Intent(interestsActivity.this, ChatInstance.class);


                    intent2.putExtra("roomnum", roomnum);

                    startActivity(intent2);
                    Log.i(TAG, "HERERERERE");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

        }.execute();
    }


    private void UpdateRequest(final JSONObject info) throws Exception {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Log.i(TAG, "PARAMS" + info);
                    JSONObject response = webb
                            .post("/update")
                            .param("params", info)
                            .ensureSuccess()
                            .asJsonObject()
                            .getBody();
                    String hashedId = null;
                    hashedId = response.getString("result");
                    Matchparams.put("id", hashedId);
                    Log.i(TAG, "RESULT: " + hashedId);

                    try {
                        MatchRequest(Matchparams);
                        Log.i(TAG, "HERERERERE575h7hh7h54");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        }.execute();
    }

}