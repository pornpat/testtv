package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.event.LoadSeriesEvent;
import com.iptv.iptv.main.event.SelectCategoryEvent;
import com.iptv.iptv.main.model.CategoryItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SeriesGridActivity extends LeanbackActivity implements FilterFragment.OnListFragmentInteractionListener {

    TextView mSeriesText;
    TextView mRecentText;
    TextView mFavoriteText;

    private int mCurrentCategory = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mSeriesText = (TextView) findViewById(R.id.series);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadSeriesEvent("http://139.59.231.135/uplay/public/api/v1/series"));
            }
        }, 500);

        mSeriesText.requestFocus();
        mSeriesText.setSelected(true);

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeriesGridActivity.this, SearchActivity.class);
                intent.putExtra("origin", "series");
                startActivity(intent);
            }
        });

        findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.layout_filter, FilterFragment.newInstance(mCurrentCategory)).commit();
                findViewById(R.id.layout_filter).setVisibility(View.VISIBLE);
            }
        });

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

    @Override
    public void onListFragmentInteraction(CategoryItem item) {
        mCurrentCategory = item.getId();
    }

    @Subscribe
    public void onFilterEvent(ApplyFilterEvent event) {
        if (event.isApplied) {
            if (mCurrentCategory != -1) {
                EventBus.getDefault().post(new LoadSeriesEvent("http://139.59.231.135/uplay/public/api/v1/series?categories_id=" + mCurrentCategory));
            }
        } else {
            EventBus.getDefault().post(new LoadSeriesEvent("http://139.59.231.135/uplay/public/api/v1/series"));
            mCurrentCategory = -1;
        }
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.layout_filter)).commit();
        findViewById(R.id.layout_filter).setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
