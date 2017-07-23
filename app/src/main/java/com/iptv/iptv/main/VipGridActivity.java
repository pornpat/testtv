package com.iptv.iptv.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.LoadVipEvent;
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

public class VipGridActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MovieMenuAdapter mAdapter;

    List<CategoryItem> category = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.appendUri(ApiUtils.VIP_FILTER_URL, ApiUtils.addToken()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                            Throwable throwable) {
                        category.add(new CategoryItem(-1, "มาใหม่", -1));
                        category.add(new CategoryItem(-1, "ยอดนิยม", -1));

                        findViewById(R.id.menu_loading).setVisibility(View.GONE);
                        mAdapter = new MovieMenuAdapter(category);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.requestFocus();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        category.add(new CategoryItem(-1, "มาใหม่", -1));
                        category.add(new CategoryItem(-1, "ยอดนิยม", -1));
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

                        findViewById(R.id.menu_loading).setVisibility(View.GONE);
                        mAdapter = new MovieMenuAdapter(category);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.requestFocus();
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadVipEvent(
                        ApiUtils.appendUri(ApiUtils.VIP_URL, ApiUtils.addToken())));
            }
        }, 500);
    }

    @Subscribe
    public void onSelectMenuEvent(SelectMenuEvent event) {
        if (event.position > 1) {
            PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);

            String url = ApiUtils.VIP_URL;
            url = ApiUtils.appendUri(url, "categories_id=" + category.get(event.position).getId());
            url = ApiUtils.appendUri(url, ApiUtils.addToken());
            EventBus.getDefault().post(new LoadVipEvent(url));
        } else {
            if (event.position == 0) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);
                EventBus.getDefault().post(new LoadVipEvent(
                        ApiUtils.appendUri(ApiUtils.VIP_URL, ApiUtils.addToken())));
            } else if (event.position == 1) {
                PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);
                EventBus.getDefault().post(new LoadVipEvent(
                        ApiUtils.appendUri(ApiUtils.VIP_HIT_URL, ApiUtils.addToken())));
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
