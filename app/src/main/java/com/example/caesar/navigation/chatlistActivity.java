package com.example.caesar.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class chatlistActivity extends Activity {

    public static final String MY_CHAT_ROOMS = "ChatRooms";
    private static final String TAG = "CHAT_lIST" ;
    static ChatListAdapter adapter;
    static ChatAdapter adapter2;
    JSONArray jsonArray = new JSONArray();
    JSONObject userinfo = new JSONObject();
    ArrayList<String> androidids = new ArrayList<String>();
    ArrayList<String> gcmids = new ArrayList<String>();
    ArrayList<String> roomhashes = new ArrayList<String>();
    ListView chatlistLV;
    ImageButton Home;
    Context ctx = this;
    ImageButton Interests;

    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        chatlistLV = (ListView) findViewById(R.id.chat_list_view);
        adapter = new ChatListAdapter(this, R.layout.single_chatlist_row);
        adapter2 = new ChatAdapter(ctx,R.layout.messageinstance);
        chatlistLV.setAdapter(adapter);
        chatlistLV.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        registerForContextMenu(chatlistLV);

        //execute code when
        chatlistLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view, int position, long id) {
                Log.i(TAG, "onListItemClick: THIS WAS THE ELEMENT CLICK ON" + position);

            }
        });

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatlistLV.setSelection(adapter.getCount() - 1);
            }
        });
        getChatlist();

    }

/*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.chat_list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(Countries[info.position]);//one of the elements
            String[] menuItems = getResources().getStringArray(R.array.menu);//list should be the populated array that holds the chat list users
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = Countries[info.position];

        TextView text = (TextView)findViewById(R.id.footer);
        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }
*/


    public static ChatAdapter getAdapter(int position) {

        if(adapter != null)
        {
            return adapter.chat_list.get(position).chat;
        }
        else return adapter2;
    }

    public void getChatlist(){
        ChatAdapter chat =  new ChatAdapter(this,R.layout.messageinstance);
        SharedPreferences appPrefs = getSharedPreferences(MY_CHAT_ROOMS, MODE_PRIVATE);
        String strJson = appPrefs.getString("userinfo", null);
        size = appPrefs.getInt("size",0);
        if(strJson != null) try {
            jsonArray = new JSONArray(strJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(size == 0)
        {
            Log.d(MY_CHAT_ROOMS, "size is 0");
        }
        else {
            Log.d(MY_CHAT_ROOMS, "size is = " + size);
            Log.d(MY_CHAT_ROOMS ,"jsonarray in chatlist = " + jsonArray);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json_data = null;
                try {
                    Log.d(MY_CHAT_ROOMS, "jsonarray subelement = " + i + " = " + jsonArray.getJSONObject(i));
                    json_data = jsonArray.getJSONObject(i);
                    adapter.add(new CLDataProvider(json_data.getString("android_id"),json_data.getString("gcm_id"), json_data.getString("room_id"),chat));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

