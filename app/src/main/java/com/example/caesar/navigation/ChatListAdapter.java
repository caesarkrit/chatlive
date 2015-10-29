package com.example.caesar.navigation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatListAdapter extends ArrayAdapter<CLDataProvider>{

    public Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://great-sarodh.c9.io/");
        }
        catch (URISyntaxException e){}
    }

    private static final String TAG = "LOL" ;
    public List<CLDataProvider> chat_list = new ArrayList<CLDataProvider>();


    private TextView date;
    private TextView usersaid;
    private TextView time;
    private TextView Message;
    private ImageView Picture;
    private String message;
    private String gcmID;
    private String androidID;
    private String roomhash;

    int type;

    Context CTX;


    public ChatListAdapter(Context context, int resource) {

        super(context, resource);
        CTX = context;
    }

    @Override
    public void add(CLDataProvider object){
        chat_list.add(object);
        super.add(object);

    }

    @Override
    public int getCount() {
        return chat_list.size();
    }

    @Override
    public CLDataProvider getItem(int position) {

        return chat_list.get(position);
    }


    @Override public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){

            LayoutInflater inflator = (LayoutInflater) CTX.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.single_chatlist_row,parent,false);
        }
        mSocket.connect();
        Message = (TextView) convertView.findViewById(R.id.messageTV);
        //  usersaid = (TextView) convertView.findViewById(R.id.usersaidTV);

        CLDataProvider provider = getItem(position);

        gcmID = provider.gcmID;
        androidID = provider.androidID;
        roomhash = provider.roomhash;
        message = provider.lastmsg;
        //       String lol = "Who? said: ";
//        usersaid.setText(lol);

//        time.setText("10:30");
        //       date.setText("9/21/15");
        Message.setText(message);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stuck
                Log.d(TAG,"JOINING ROOMID" + chat_list.get(position).roomhash );
                mSocket.emit("CreateRoom",chat_list.get(position).roomhash,chat_list.get(position).gcmID);
                Intent intent2 = new Intent(CTX, ChatInstance.class);
                intent2.putExtra("roomnum", chat_list.get(position).roomhash);

                CTX.startActivity(intent2);

            }
        });


        notifyDataSetChanged();
        return convertView;

    }


}