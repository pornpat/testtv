package com.iptv.iptv.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.SelectCategoryEvent;

import org.greenrobot.eventbus.EventBus;

public class SeriesGridActivity extends LeanbackActivity {

    TextView mSeriesText;
    TextView mRecentText;
    TextView mFavoriteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mSeriesText = (TextView) findViewById(R.id.series);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        mSeriesText.requestFocus();
        mSeriesText.setSelected(true);

        mSeriesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Series"));
                setTextSelected(mSeriesText);
            }
        });

        mRecentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Recent"));
                setTextSelected(mRecentText);
            }
        });

        mFavoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Favorite"));
                setTextSelected(mFavoriteText);
            }
        });
    }

    private void setTextSelected(TextView currentText) {
        mSeriesText.setSelected(false);
        mRecentText.setSelected(false);
        mFavoriteText.setSelected(false);

        currentText.setSelected(true);
    }
}
