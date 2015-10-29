package com.example.caesar.navigation;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabhost = getTabHost();

        TabHost.TabSpec tab1 = tabhost.newTabSpec("tab1");
        tab1.setIndicator("Interests");
        Intent i1 = new Intent(MainActivity.this,interestsActivity.class);
        tab1.setContent(i1);

        TabHost.TabSpec tab2 = tabhost.newTabSpec("tab2");
        tab2.setIndicator("Chats");
        Intent i2 = new Intent(MainActivity.this,chatlistActivity.class);
        tab2.setContent(i2);


        TabHost.TabSpec tab3 = tabhost.newTabSpec("tab3");
        tab3.setIndicator("Feed");
        Intent i3 = new Intent(MainActivity.this,feedActivity.class);
        tab3.setContent(i3);

        tabhost.addTab(tab1);
        tabhost.addTab(tab2);
        tabhost.addTab(tab3);


    }



}
