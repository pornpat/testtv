package com.iptv.iptv.main;

import android.os.Bundle;
import android.view.View;

import com.iptv.iptv.R;

public class MovieGridActivity extends LeanbackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, MovieGridFragment.newInstance("initial")).commit();

        findViewById(R.id.movie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, MovieGridFragment.newInstance("movie")).commit();
            }
        });

        findViewById(R.id.recent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, MovieGridFragment.newInstance("recent")).commit();
            }
        });

        findViewById(R.id.favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, MovieGridFragment.newInstance("favorite")).commit();
            }
        });
    }
}
