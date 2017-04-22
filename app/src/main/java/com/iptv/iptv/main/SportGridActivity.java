package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.event.LoadSportEvent;
import com.iptv.iptv.main.model.CategoryItem;
import com.iptv.iptv.main.model.CountryItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SportGridActivity extends LeanbackActivity implements FilterFragment.OnCategoryInteractionListener,
        FilterFragment.OnCountryInteractionListener, FilterFragment.OnYearInteractionListener {

    TextView mSportText;
    TextView mRecentText;
    TextView mFavoriteText;

    private int mCurrentCategory = -1;
    private int mCurrentCountry = -1;
    private int mCurrentYear = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mSportText = (TextView) findViewById(R.id.sport);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadSportEvent(
                        UrlUtil.appendUri(UrlUtil.SPORT_URL, UrlUtil.addToken())));
            }
        }, 500);

        mSportText.requestFocus();
        mSportText.setSelected(true);

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SportGridActivity.this, SearchActivity.class);
                intent.putExtra("origin", "sport");
                startActivity(intent);
            }
        });

        findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.layout_filter,
                        FilterFragment.newInstance(UrlUtil.appendUri(UrlUtil.SPORT_FILTER_URL, UrlUtil.addToken()),
                                mCurrentCategory, mCurrentCountry, mCurrentYear)).commit();
                findViewById(R.id.layout_filter).setVisibility(View.VISIBLE);
            }
        });

        mSportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadSportEvent(
                        UrlUtil.appendUri(UrlUtil.SPORT_URL, UrlUtil.addToken())));
                setTextSelected(mSportText);
            }
        });

        mRecentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadSportEvent(
                        UrlUtil.appendUri(UrlUtil.HISTORY_URL, UrlUtil.addToken())));
                setTextSelected(mRecentText);
            }
        });

        mFavoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadSportEvent(
                        UrlUtil.appendUri(UrlUtil.FAVORITE_URL, UrlUtil.addToken())));
                setTextSelected(mFavoriteText);
            }
        });
    }

    private void setTextSelected(TextView currentText) {
        mSportText.setSelected(false);
        mRecentText.setSelected(false);
        mFavoriteText.setSelected(false);

        currentText.setSelected(true);
    }

    @Override
    public void onCategoryInteraction(CategoryItem item) {
        mCurrentCategory = item.getId();
    }

    @Override
    public void onCountryInteraction(CountryItem item) {
        mCurrentCountry = item.getId();
    }

    @Override
    public void onYearInteraction(int year) {
        mCurrentYear = year;
    }

    @Subscribe
    public void onFilterEvent(ApplyFilterEvent event) {
        if (event.isApplied) {
            String url = UrlUtil.SPORT_URL;
            if (mCurrentCategory != -1) {
                url = UrlUtil.appendUri(url, "categories_id=" + mCurrentCategory);
            }
            if (mCurrentCountry != -1) {
                url = UrlUtil.appendUri(url, "countries_id=" + mCurrentCountry);
            }
            if (mCurrentYear != -1) {
                url = UrlUtil.appendUri(url, "year=" + mCurrentYear);
            }
            url = UrlUtil.appendUri(url, UrlUtil.addToken());

            EventBus.getDefault().post(new LoadSportEvent(url));
        } else {
            EventBus.getDefault().post(new LoadSportEvent(
                    UrlUtil.appendUri(UrlUtil.SPORT_URL, UrlUtil.addToken())));
            mCurrentCategory = -1;
            mCurrentCountry = -1;
            mCurrentYear = -1;
        }
        setTextSelected(mSportText);
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