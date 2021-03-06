package com.iptv.iptv.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iptv.iptv.R;
import com.iptv.iptv.main.ApiUtils;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.PrefUtils;
import com.iptv.iptv.main.SeriesChoiceAdapter;
import com.iptv.iptv.main.SeriesRecommendAdapter;
import com.iptv.iptv.main.data.SeriesDataUtil;
import com.iptv.iptv.main.event.ChoiceEvent;
import com.iptv.iptv.main.event.RecommendEvent;
import com.iptv.iptv.main.model.SeriesItem;
import com.iptv.iptv.main.model.SeriesTrackItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SeriesDetailsActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    public static final String SERIES = "Series";

    private SeriesItem mSelectedMovie;

    private ImageView mImage;
    private TextView mEngTitle;
    private TextView mTitle;
    private TextView mDetail;
    private RecyclerView mChoiceList;
    private RecyclerView mRecommendList;

    private View mLoading;
    private View mContent;

    private boolean isFav = false;
    private List<SeriesItem> mRecommend;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSelectedMovie = Parcels.unwrap(getIntent().getParcelableExtra(SeriesDetailsActivity.SERIES));

        mLoading = findViewById(R.id.loading);
        mContent = findViewById(R.id.layout_content);

        mImage = (ImageView) findViewById(R.id.img);
        mEngTitle = (TextView) findViewById(R.id.txt_eng_title);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mDetail = (TextView) findViewById(R.id.txt_detail);
        mChoiceList = (RecyclerView) findViewById(R.id.list_choice);
        mChoiceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecommendList = (RecyclerView) findViewById(R.id.list_recommend);
        mRecommendList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Glide.with(getApplicationContext()).load(mSelectedMovie.getImageUrl()).placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder).override(300, 450).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                mImage.setImageResource(R.drawable.movie_placeholder);
                return true;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mImage.setImageDrawable(resource);
                return true;
            }
        }).into(mImage);

        mEngTitle.setText(mSelectedMovie.getEngName());
        mTitle.setText(mSelectedMovie.getName());
        mDetail.setText(mSelectedMovie.getDescription());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.appendUri(ApiUtils.getFavCheckUrl(mSelectedMovie.getId()), ApiUtils.addToken()), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showChoice();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    isFav = jsonObject.getBoolean("isFavorite");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showChoice();
            }
        });

        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.get(ApiUtils.appendUri(ApiUtils.getRecommendUrl(ApiUtils.SERIES_URL, mSelectedMovie.getId()), ApiUtils.addToken()), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mRecommend = SeriesDataUtil.getSeriesListFromJson(responseString);
                mRecommendList.setAdapter(new SeriesRecommendAdapter(SeriesDetailsActivity.this, mRecommend));
            }
        });

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void showChoice() {
        List<SeriesTrackItem> trackList = new ArrayList<>();
        for (int i = 0; i < mSelectedMovie.getTracks().size(); i++) {
            trackList.add(mSelectedMovie.getTracks().get(i));
        }
        if (trackList.size() == 0) {
            Toast.makeText(this, "วันใช้งานของคุณหมด กรุณาเติมวันใช้งานเพื่อรับชม", Toast.LENGTH_SHORT).show();
        }
        trackList.add(new SeriesTrackItem());
        mChoiceList.setAdapter(new SeriesChoiceAdapter(SeriesDetailsActivity.this, trackList, isFav));
        mChoiceList.requestFocus();

        showContent(true);
    }

    private void showContent(boolean show) {
        if (show) {
            mLoading.setVisibility(View.GONE);
            mContent.setVisibility(View.VISIBLE);
        } else {
            mLoading.setVisibility(View.VISIBLE);
            mContent.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onChoiceEvent(final ChoiceEvent event) {
        if (event.position < 0) {
            PrefUtils.setBooleanProperty(R.string.pref_update_series, true);
            if (!isFav) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(ApiUtils.appendUri(
                        ApiUtils.addMediaId(ApiUtils.FAVORITE_URL, mSelectedMovie.getId()),
                        ApiUtils.addToken()), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                            Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    }
                });
                isFav = true;
                showChoice();
            } else {
                AsyncHttpClient client = new AsyncHttpClient();
                client.delete(ApiUtils.appendUri(
                        ApiUtils.addMediaId(ApiUtils.FAVORITE_URL, mSelectedMovie.getId()),
                        ApiUtils.addToken()), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                            Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    }
                });
                isFav = false;
                showChoice();
            }
        } else {
            final ProgressDialog progress = new ProgressDialog(SeriesDetailsActivity.this);
            progress.setMessage("โปรดรอ...");
            progress.show();

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(ApiUtils.appendUri(ApiUtils.EXPIRE_CHECK_URL, ApiUtils.addToken()), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(SeriesDetailsActivity.this, "กรุณาลองใหม่ในภายหลัง", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        boolean isExpired = jsonObject.getBoolean("expired");
                        if (!isExpired) {
                            Intent intent = new Intent(SeriesDetailsActivity.this, SeriesEpisodeActivity.class);
                            intent.putExtra(SeriesDetailsActivity.SERIES, Parcels.wrap(mSelectedMovie));
                            intent.putExtra("track", event.position);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SeriesDetailsActivity.this, "วันใช้งานของคุณหมด กรุณาเติมเวันใช้งานเพื่อรับชม", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progress.dismiss();
                }
            });
        }
    }

    @Subscribe
    public void onRecommendEvent(RecommendEvent event) {
        Intent intent = new Intent(SeriesDetailsActivity.this, SeriesDetailsActivity.class);
        intent.putExtra(SeriesDetailsActivity.SERIES, Parcels.wrap(mRecommend.get(event.position)));
        intent.putExtra(getResources().getString(R.string.should_start), true);
        startActivity(intent);
        finish();
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
