package com.example.caesar.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class feedActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


        final SwipeRefreshLayout swipeview = (SwipeRefreshLayout) findViewById(R.id.swipe);
     //   final TextView rndNum = (TextView) findViewById(R.id.lb1);
      //  final TextView swipingText = (TextView) findViewById(R.id.swipeinfo);


        swipeview.setColorSchemeResources(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
        swipeview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //swipingText.setVisibility(View.GONE);
                swipeview.setRefreshing(true);
                Log.d("swipe", "REfreshing number");
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //what ever refreshes goes in here
                    }
                }, 3000);
            }
        });
    }

}

