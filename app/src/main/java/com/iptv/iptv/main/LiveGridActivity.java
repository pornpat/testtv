package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.LoadLiveEvent;
import com.iptv.iptv.main.event.SelectMenuEvent;
import com.iptv.iptv.main.model.CategoryItem;
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

public class LiveGridActivity extends AppCompatActivity implements
        LiveScheduleFragment.OnListFragmentInteractionListener {

    RecyclerView mRecyclerView;
    MovieMenuAdapter mAdapter;

    List<CategoryItem> category = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.appendUri(ApiUtils.LIVE_FILTER_URL, ApiUtils.addToken()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                            Throwable throwable) {
                        category.add(new CategoryItem(-1, "ทั้งหมด", -1));
                        category.add(new CategoryItem(-1, "รับชมล่าสุด", -1));
                        category.add(new CategoryItem(-1, "รายการโปรด", -1));

                        findViewById(R.id.menu_loading).setVisibility(View.GONE);
                        mAdapter = new MovieMenuAdapter(category);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.requestFocus();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        category.add(new CategoryItem(-1, "ทั้งหมด", -1));
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
                EventBus.getDefault().post(new LoadLiveEvent(
                        ApiUtils.appendUri(ApiUtils.LIVE_URL, ApiUtils.addToken())));
            }
        }, 500);

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiveGridActivity.this, SearchActivity.class);
                intent.putExtra("origin", "live");
                startActivity(intent);
            }
        });

        findViewById(R.id.table).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.layout_table,
                        LiveScheduleFragment.newInstance()).commit();
                findViewById(R.id.layout_table).setVisibility(View.VISIBLE);
                findViewById(R.id.grid_fragment).setVisibility(View.GONE);
            }
        });
    }

    @Subscribe
    public void onSelectMenuEvent(SelectMenuEvent event) {
        if (event.position > 0 && event.position < category.size() - 2) {
            PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);

            String url = ApiUtils.LIVE_URL;
            url = ApiUtils.appendUri(url, "categories_id=" + category.get(event.position).getId());
            url = ApiUtils.appendUri(url, ApiUtils.addToken());
            EventBus.getDefault().post(new LoadLiveEvent(url));
        } else {
            if (event.position == 0) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);
                EventBus.getDefault().post(new LoadLiveEvent(
                        ApiUtils.appendUri(ApiUtils.LIVE_URL, ApiUtils.addToken())));
            } else if (event.position == category.size() - 2) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);
                EventBus.getDefault().post(new LoadLiveEvent(
                        ApiUtils.appendUri(ApiUtils.LIVE_HISTORY_URL, ApiUtils.addToken())));
            } else if (event.position == category.size() - 1) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, true);
                EventBus.getDefault().post(new LoadLiveEvent(
                        ApiUtils.appendUri(ApiUtils.LIVE_FAVORITE_URL, ApiUtils.addToken())));
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

    @Override
    public void onListFragmentInteraction(String streamKey) {
        Log.v("testkn", streamKey);
    }
}