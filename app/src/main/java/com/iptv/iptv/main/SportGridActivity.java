package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.event.LoadSportEvent;
import com.iptv.iptv.main.model.CountryItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SportGridActivity extends AppCompatActivity implements FilterFragment.OnCountryInteractionListener,
        FilterFragment.OnYearInteractionListener {

    TextView mSportText;
    TextView mHitText;
    TextView mRecentText;
    TextView mFavoriteText;

    private int mCurrentCountry = -1;
    private int mCurrentYear = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSportText = (TextView) findViewById(R.id.sport);
        mHitText = (TextView) findViewById(R.id.hit);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_URL, ApiUtils.addToken())));
            }
        }, 500);

        mSportText.requestFocus();
        setTextSelected(mSportText);

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
                        FilterFragment.newInstance(
                                ApiUtils.appendUri(ApiUtils.SPORT_FILTER_URL, ApiUtils.addToken()),
                                mCurrentCountry, mCurrentYear)).commit();
                findViewById(R.id.layout_filter).setVisibility(View.VISIBLE);
                findViewById(R.id.grid_fragment).setVisibility(View.GONE);
            }
        });

        mSportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_URL, ApiUtils.addToken())));
                setTextSelected(mSportText);
                clearFilter();
            }
        });

        mHitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_HIT_URL, ApiUtils.addToken())));
                setTextSelected(mHitText);
                clearFilter();
            }
        });

        mRecentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_HISTORY_URL, ApiUtils.addToken())));
                setTextSelected(mRecentText);
                clearFilter();
            }
        });

        mFavoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_FAVORITE_URL, ApiUtils.addToken())));
                setTextSelected(mFavoriteText);
                clearFilter();
            }
        });
    }

    private void setTextSelected(TextView currentText) {
        mSportText.setSelected(false);
        mHitText.setSelected(false);
        mRecentText.setSelected(false);
        mFavoriteText.setSelected(false);

        currentText.setSelected(true);

        if (currentText == mFavoriteText) {
            PrefUtils.setBooleanProperty(R.string.pref_current_favorite, true);
        } else {
            PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);
        }
    }

    private void clearFilter() {
        mCurrentCountry = -1;
        mCurrentYear = -1;
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
            String url = ApiUtils.SPORT_URL;
            if (mCurrentCountry != -1) {
                url = ApiUtils.appendUri(url, "countries_id=" + mCurrentCountry);
            }
            if (mCurrentYear != -1) {
                url = ApiUtils.appendUri(url, "year=" + mCurrentYear);
            }
            url = ApiUtils.appendUri(url, ApiUtils.addToken());

            EventBus.getDefault().post(new LoadSportEvent(url));
        } else {
            EventBus.getDefault().post(new LoadSportEvent(
                    ApiUtils.appendUri(ApiUtils.SPORT_URL, ApiUtils.addToken())));
            mCurrentCountry = -1;
            mCurrentYear = -1;
        }
        setTextSelected(mSportText);
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