package com.iptv.iptv.main;

import android.os.Bundle;
import android.view.View;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.SelectCategoryEvent;

import org.greenrobot.eventbus.EventBus;

public class MovieGridActivity extends LeanbackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        findViewById(R.id.movie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Movie"));
            }
        });

        findViewById(R.id.recent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Recent"));
            }
        });

        findViewById(R.id.favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Favorite"));
            }
        });
    }
}
