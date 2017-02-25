package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.RelativeLayout;

import com.iptv.iptv.R;

public class HomeActivity extends LeanbackActivity {

    RelativeLayout mLiveButton;
    RelativeLayout mRecommendButton;
    RelativeLayout mSportButton;
    RelativeLayout mFavouriteButton;
    RelativeLayout mMovieButton;
    RelativeLayout mSeriesButton;
    RelativeLayout mAdvertiseButton;
    RelativeLayout mSettingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mLiveButton = (RelativeLayout) findViewById(R.id.btn_live);
        mRecommendButton = (RelativeLayout) findViewById(R.id.btn_recommend);
        mSportButton = (RelativeLayout) findViewById(R.id.btn_sport);
        mFavouriteButton = (RelativeLayout) findViewById(R.id.btn_favourite);
        mMovieButton = (RelativeLayout) findViewById(R.id.btn_movie);
        mSeriesButton = (RelativeLayout) findViewById(R.id.btn_series);
        mAdvertiseButton = (RelativeLayout) findViewById(R.id.btn_advertise);
        mSettingButton = (RelativeLayout) findViewById(R.id.btn_setting);

        mLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LiveActivity.class);
                startActivity(intent);
            }
        });

        mMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MovieGridActivity.class);
                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this)
                                .toBundle();
                startActivity(intent, bundle);
            }
        });

    }
}
