package com.iptv.iptv.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.ApiUtils;
import com.iptv.iptv.main.LiveScheduleFragment;
import com.iptv.iptv.main.MovieMenuAdapter;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.PrefUtils;
import com.iptv.iptv.main.event.LoadLiveEvent;
import com.iptv.iptv.main.event.SelectMenuEvent;
import com.iptv.iptv.main.model.CategoryItem;
import com.iptv.iptv.main.model.LiveItem;
import com.iptv.iptv.main.model.LiveProgramItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class LiveGridActivity extends AppCompatActivity implements
        LiveScheduleFragment.OnListFragmentInteractionListener,
        NetworkStateReceiver.NetworkStateReceiverListener {

    RecyclerView mRecyclerView;
    MovieMenuAdapter mAdapter;

    List<CategoryItem> category = new ArrayList<>();

    private int currentPosition = 0;

    private NetworkStateReceiver networkStateReceiver;

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
                        category.add(new CategoryItem(-1, "ตารางแข่งขัน", -1));
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
                        category.add(new CategoryItem(-1, "ตารางแข่งขัน", -1));
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

        findViewById(R.id.search).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    Intent intent = new Intent(LiveGridActivity.this, SearchActivity.class);
                    intent.putExtra("origin", "live");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.search).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(LiveGridActivity.this, SearchActivity.class);
                    intent.putExtra("origin", "live");
                    startActivity(intent);
                }
                return false;
            }
        });

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Subscribe
    public void onSelectMenuEvent(SelectMenuEvent event) {
        if (event.position > 1 && event.position < category.size() - 2) {
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
            } else if (event.position == 1) {
                getFragmentManager().beginTransaction().replace(R.id.layout_table,
                        LiveScheduleFragment.newInstance()).commit();
                findViewById(R.id.layout_table).setVisibility(View.VISIBLE);
                findViewById(R.id.grid_fragment).setVisibility(View.GONE);
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
        if (currentPosition == 1 && event.position != 1) {
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.layout_table)).commit();
            findViewById(R.id.layout_table).setVisibility(View.GONE);
            findViewById(R.id.grid_fragment).setVisibility(View.VISIBLE);
        }
        currentPosition = event.position;
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
        final ProgressDialog pDialog = new ProgressDialog(LiveGridActivity.this);
        pDialog.setMessage("โปรดรอ...");
        pDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.appendUri(ApiUtils.getLiveUrlByKey(streamKey), ApiUtils.addToken()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                            Throwable throwable) {
                        Toast.makeText(LiveGridActivity.this, "เกิดความผิดพลาด กรุณาลองใหม่ในภายหลัง", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            if (jsonObject.has("live")) {
                                final List<LiveItem> list = new ArrayList<>();
                                LiveItem item = new LiveItem();

                                JSONObject liveObj = jsonObject.getJSONObject("live");
                                item.setId(jsonObject.getInt("id"));
                                item.setName(liveObj.getString("name"));
                                item.setLogoUrl(liveObj.getString("logo_url"));
                                item.setUrl(liveObj.getString("url"));
                                item.setFav(jsonObject.getBoolean("is_favorite"));

                                List<LiveProgramItem> programs = new ArrayList<>();
                                JSONArray programArray = liveObj.getJSONArray("programs");
                                for (int j = 0; j < programArray.length(); j++) {
                                    JSONObject program = programArray.getJSONObject(j);
                                    String p_name = program.getString("name");
                                    JSONObject start = program.getJSONObject("start_time");
                                    int startHour = Integer.parseInt(start.getString("hour"));
                                    int startMin = Integer.parseInt(start.getString("minute"));
                                    JSONObject end = program.getJSONObject("end_time");
                                    int endHour = Integer.parseInt(end.getString("hour"));
                                    int endMin = Integer.parseInt(end.getString("minute"));
                                    programs.add(new LiveProgramItem(p_name, startHour, startMin, endHour, endMin));
                                }
                                item.setPrograms(programs);

                                list.add(item);

                                AsyncHttpClient client = new AsyncHttpClient();
                                client.get(ApiUtils.appendUri(ApiUtils.EXPIRE_CHECK_URL, ApiUtils.addToken()), new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Toast.makeText(LiveGridActivity.this, "กรุณาลองใหม่ในภายหลัง", Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(responseString);
                                            boolean isExpired = jsonObject.getBoolean("expired");
                                            if (!isExpired) {
                                                Intent intent = new Intent(LiveGridActivity.this, LivePlayerActivity.class);
                                                intent.putExtra("position", 0);
                                                intent.putExtra("list", Parcels.wrap(list));
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(LiveGridActivity.this, "วันใช้งานของคุณหมด กรุณาเติมเวันใช้งานเพื่อรับชม", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        pDialog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(LiveGridActivity.this, "ขออภัย ช่องดังกล่าวยังไม่พร้อมใช้งาน", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "Network unavailable.. Please check your wifi-connection", Toast.LENGTH_LONG).show();
    }

    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }
}