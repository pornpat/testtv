package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.event.LoadMovieEvent;
import com.iptv.iptv.main.model.CategoryItem;
import com.iptv.iptv.main.model.CountryItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MovieGridActivity extends LeanbackActivity implements FilterFragment.OnCategoryInteractionListener,
        FilterFragment.OnCountryInteractionListener, FilterFragment.OnYearInteractionListener {

    TextView mMovieText;
    TextView mHitText;
    TextView mRecentText;
    TextView mFavoriteText;

    private int mCurrentCategory = -1;
    private int mCurrentCountry = -1;
    private int mCurrentYear = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mMovieText = (TextView) findViewById(R.id.movie);
        mHitText = (TextView) findViewById(R.id.hit);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadMovieEvent(
                        ApiUtils.appendUri(ApiUtils.MOVIE_URL, ApiUtils.addToken())));
            }
        }, 500);

        mMovieText.requestFocus();
        setTextSelected(mMovieText);

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieGridActivity.this, SearchActivity.class);
                intent.putExtra("origin", "movie");
                startActivity(intent);
            }
        });

        findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.layout_filter,
                        FilterFragment.newInstance(
                                ApiUtils.appendUri(ApiUtils.MOVIE_FILTER_URL, ApiUtils.addToken()),
                                mCurrentCategory, mCurrentCountry, mCurrentYear)).commit();
                findViewById(R.id.layout_filter).setVisibility(View.VISIBLE);
                findViewById(R.id.grid_fragment).setVisibility(View.GONE);
            }
        });

        mMovieText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadMovieEvent(
                        ApiUtils.appendUri(ApiUtils.MOVIE_URL, ApiUtils.addToken())));
                setTextSelected(mMovieText);
                clearFilter();
            }
        });

        mHitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadMovieEvent(
                        ApiUtils.appendUri(ApiUtils.MOVIE_HIT_URL, ApiUtils.addToken())));
                setTextSelected(mHitText);
                clearFilter();
            }
        });

        mRecentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadMovieEvent(
                        ApiUtils.appendUri(ApiUtils.MOVIE_HISTORY_URL, ApiUtils.addToken())));
                setTextSelected(mRecentText);
                clearFilter();
            }
        });

        mFavoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadMovieEvent(
                        ApiUtils.appendUri(ApiUtils.MOVIE_FAVORITE_URL, ApiUtils.addToken())));
                setTextSelected(mFavoriteText);
                clearFilter();
            }
        });
    }

    private void setTextSelected(TextView currentText) {
        mMovieText.setSelected(false);
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
            String url = ApiUtils.MOVIE_URL;
            if (mCurrentCategory != -1) {
                url = ApiUtils.appendUri(url, "categories_id=" + mCurrentCategory);
            }
            if (mCurrentCountry != -1) {
                url = ApiUtils.appendUri(url, "countries_id=" + mCurrentCountry);
            }
            if (mCurrentYear != -1) {
                url = ApiUtils.appendUri(url, "year=" + mCurrentYear);
            }
            url = ApiUtils.appendUri(url, ApiUtils.addToken());

            EventBus.getDefault().post(new LoadMovieEvent(url));
        } else {
            EventBus.getDefault().post(new LoadMovieEvent(
                    ApiUtils.appendUri(ApiUtils.MOVIE_URL, ApiUtils.addToken())));
            mCurrentCategory = -1;
            mCurrentCountry = -1;
            mCurrentYear = -1;
        }
        setTextSelected(mMovieText);
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
