package com.example.caesar.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends ArrayAdapter<DataProvider>{

    public List<DataProvider> chat_list = new ArrayList<DataProvider>();

    private TextView CHAT_TXT;
    private ImageView Picture;

    int type;

    Context CTX;


    public ChatAdapter(Context context, int resource) {

        super(context, resource);
        CTX = context;
    }

    @Override
    public void add(DataProvider object){
        chat_list.add(object);
        super.add(object);

    }

    @Override
    public int getCount() {
        return chat_list.size();
    }

    @Override
    public DataProvider getItem(int position) {

        return chat_list.get(position);
    }


    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){


            LayoutInflater inflator = (LayoutInflater) CTX.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.messageinstance,parent,false);
        }
        CHAT_TXT = (TextView) convertView.findViewById(R.id.singleMessage);
        Picture = (ImageView) convertView.findViewById(R.id.imageView2);
        String Message;
        Bitmap pictures;
        ImageView newpic;

        boolean POSITION;
        DataProvider provider = getItem(position);
        LinearLayout.LayoutParams imgLayout;
        pictures = provider.image;
        Message = provider.message;
        CHAT_TXT.setText(Message);
        type = provider.getType();

        POSITION = provider.position;
        //CHAT_TXT.setBackgroundResource(POSITION ? R.drawable.bubble_b : R.drawable.bubble_a);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        switch (type) {
            case DataProvider.TYPE_MESSAGE:
                Picture.setVisibility(View.GONE);
                CHAT_TXT.setVisibility(View.VISIBLE);
                if(provider.side) {
                    params.gravity = Gravity.LEFT;
                    CHAT_TXT.setBackgroundResource(R.drawable.bubble_b);
                    break;
                } else {
                    params.gravity = Gravity.RIGHT;
                    CHAT_TXT.setBackgroundResource(R.drawable.bubble_a);
                    break;
                }
            case DataProvider.TYPE_LOG:
                Picture.setVisibility(View.GONE);
                params.gravity = Gravity.RIGHT;
                CHAT_TXT.setBackgroundResource(R.drawable.bubble_a);
                break;
            case DataProvider.TYPE_ACTION:
                Picture.setVisibility(View.GONE);
                params.gravity = Gravity.LEFT;
                CHAT_TXT.setBackgroundResource(R.drawable.rectangle);
                break;
            case DataProvider.TYPE_IMG:
                CHAT_TXT.setVisibility(View.GONE);
                Picture.setVisibility(View.VISIBLE);
                params = new LinearLayout.LayoutParams(500,500);
                if(provider.side) {
                    params.gravity = Gravity.LEFT;
                } else {
                    params.gravity = Gravity.RIGHT;
                }
                Picture.setImageBitmap(pictures);
                Picture.setLayoutParams(params);
                break;

        }


        CHAT_TXT.setLayoutParams(params);
        return convertView;
    }


}

