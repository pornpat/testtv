package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.event.LoadSportEvent;
import com.iptv.iptv.main.event.SelectMenuEvent;
import com.iptv.iptv.main.model.CategoryItem;
import com.iptv.iptv.main.model.CountryItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SportGridActivity extends AppCompatActivity implements FilterFragment.OnCountryInteractionListener,
        FilterFragment.OnYearInteractionListener {

    RecyclerView mRecyclerView;
    MovieMenuAdapter mAdapter;

    List<CategoryItem> category = new ArrayList<>();

    private int mCurrentCountry = -1;
    private int mCurrentYear = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.appendUri(ApiUtils.SPORT_FILTER_URL, ApiUtils.addToken()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                            Throwable throwable) {
                        category.add(new CategoryItem(-1, "รีรันมาใหม่", -1));
                        category.add(new CategoryItem(-1, "รีรันยอดนิยม", -1));
                        category.add(new CategoryItem(-1, "รับชมล่าสุด", -1));
                        category.add(new CategoryItem(-1, "รายการโปรด", -1));

                        findViewById(R.id.menu_loading).setVisibility(View.GONE);
                        mAdapter = new MovieMenuAdapter(category);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.requestFocus();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        category.add(new CategoryItem(-1, "รีรันมาใหม่", -1));
                        category.add(new CategoryItem(-1, "รีรันยอดนิยม", -1));
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            JSONArray categoryArray = jsonObject.getJSONArray("categories");
                            for (int i = 0; i < categoryArray.length(); i++) {
                                JSONObject categoryObject = categoryArray.getJSONObject(i);
                                category.add(new CategoryItem(categoryObject.getInt("id"), categoryObject.getString("name"),
                                        categoryObject.getInt("order")));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        category.add(new CategoryItem(-1, "รับชมล่าสุด", -1));
                        category.add(new CategoryItem(-1, "รายการโปรด", -1));

                        findViewById(R.id.menu_loading).setVisibility(View.GONE);
                        mAdapter = new MovieMenuAdapter(category);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.requestFocus();
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_URL, ApiUtils.addToken())));
            }
        }, 500);

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

        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.layout_filter)).commit();
        findViewById(R.id.layout_filter).setVisibility(View.GONE);
        findViewById(R.id.grid_fragment).setVisibility(View.VISIBLE);

        mAdapter = new MovieMenuAdapter(category);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.requestFocus();
    }

    @Subscribe
    public void onSelectMenuEvent(SelectMenuEvent event) {
        if (event.position > 1 && event.position < category.size() - 2) {
            PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);

            String url = ApiUtils.SPORT_URL;
            url = ApiUtils.appendUri(url, "categories_id=" + category.get(event.position).getId());
            url = ApiUtils.appendUri(url, ApiUtils.addToken());
            EventBus.getDefault().post(new LoadSportEvent(url));

            clearFilter();
        } else {
            if (event.position == 0) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_URL, ApiUtils.addToken())));
                clearFilter();
            } else if (event.position == 1) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_HIT_URL, ApiUtils.addToken())));
                clearFilter();
            } else if (event.position == category.size() - 2) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_HISTORY_URL, ApiUtils.addToken())));
                clearFilter();
            } else if (event.position == category.size() - 1) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, true);
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_FAVORITE_URL, ApiUtils.addToken())));
                clearFilter();
            }
        }
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