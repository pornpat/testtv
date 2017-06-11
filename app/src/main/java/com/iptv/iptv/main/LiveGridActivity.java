package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.event.LoadLiveEvent;
import com.iptv.iptv.main.model.CategoryItem;
import com.iptv.iptv.main.model.CountryItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LiveGridActivity extends LeanbackActivity implements FilterFragment.OnCategoryInteractionListener,
        FilterFragment.OnCountryInteractionListener, FilterFragment.OnYearInteractionListener {

    TextView mLiveText;
    TextView mRecentText;
    TextView mFavoriteText;

    private int mCurrentCategory = -1;
    private int mCurrentCountry = -1;
    private int mCurrentYear = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mLiveText = (TextView) findViewById(R.id.live);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadLiveEvent(
                        UrlUtil.appendUri(UrlUtil.LIVE_URL, UrlUtil.addToken())));
            }
        }, 500);

        mLiveText.requestFocus();
        setTextSelected(mLiveText);

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiveGridActivity.this, SearchActivity.class);
                intent.putExtra("origin", "live");
                startActivity(intent);
            }
        });

        findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.layout_filter,
                        FilterFragment.newInstance(UrlUtil.appendUri(UrlUtil.LIVE_FILTER_URL, UrlUtil.addToken()),
                                mCurrentCategory, mCurrentCountry, mCurrentYear)).commit();
                findViewById(R.id.layout_filter).setVisibility(View.VISIBLE);
                findViewById(R.id.grid_fragment).setVisibility(View.GONE);
            }
        });

        mLiveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadLiveEvent(
                        UrlUtil.appendUri(UrlUtil.LIVE_URL, UrlUtil.addToken())));
                setTextSelected(mLiveText);
                clearFilter();
            }
        });

        mRecentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadLiveEvent(
                        UrlUtil.appendUri(UrlUtil.LIVE_HISTORY_URL, UrlUtil.addToken())));
                setTextSelected(mRecentText);
                clearFilter();
            }
        });

        mFavoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadLiveEvent(
                        UrlUtil.appendUri(UrlUtil.LIVE_FAVORITE_URL, UrlUtil.addToken())));
                setTextSelected(mFavoriteText);
                clearFilter();
            }
        });
    }

    private void setTextSelected(TextView currentText) {
        mLiveText.setSelected(false);
        mRecentText.setSelected(false);
        mFavoriteText.setSelected(false);

        currentText.setSelected(true);

        if (currentText == mFavoriteText) {
            PrefUtil.setBooleanProperty(R.string.pref_current_favorite, true);
        } else {
            PrefUtil.setBooleanProperty(R.string.pref_current_favorite, false);
        }
    }

    private void clearFilter() {
        mCurrentCategory = -1;
        mCurrentCountry = -1;
        mCurrentYear = -1;
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
            String url = UrlUtil.LIVE_URL;
            if (mCurrentCategory != -1) {
                url = UrlUtil.appendUri(url, "categories_id=" + mCurrentCategory);
            }
            url = UrlUtil.appendUri(url, UrlUtil.addToken());

            EventBus.getDefault().post(new LoadLiveEvent(url));
        } else {
            EventBus.getDefault().post(new LoadLiveEvent(
                    UrlUtil.appendUri(UrlUtil.LIVE_URL, UrlUtil.addToken())));
            mCurrentCategory = -1;
            mCurrentCountry = -1;
            mCurrentYear = -1;
        }
        setTextSelected(mLiveText);
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.layout_filter)).commit();
        findViewById(R.id.layout_filter).setVisibility(View.GONE);
        findViewById(R.id.grid_fragment).setVisibility(View.VISIBLE);
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