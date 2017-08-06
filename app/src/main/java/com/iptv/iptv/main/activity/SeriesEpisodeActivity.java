package com.iptv.iptv.main.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iptv.iptv.R;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.SeriesEpisodeAdapter;
import com.iptv.iptv.main.event.SelectEpisodeEvent;
import com.iptv.iptv.main.model.SeriesItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

public class SeriesEpisodeActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    private static SeriesItem mSelectSeries;
    private static int track;

    RecyclerView mRecyclerView;
    ImageView mImage;
    TextView mTitleText;
    TextView mEngTitleText;
    TextView mAudioText;
    TextView mSubtitleText;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSelectSeries = Parcels.unwrap(getIntent().getParcelableExtra(SeriesDetailsActivity.SERIES));
        track = getIntent().getExtras().getInt("track");

        mImage = (ImageView) findViewById(R.id.img);
        mTitleText = (TextView) findViewById(R.id.txt_title);
        mEngTitleText = (TextView) findViewById(R.id.txt_eng_title);
        mAudioText = (TextView) findViewById(R.id.txt_audio);
        mSubtitleText = (TextView) findViewById(R.id.txt_subtitle);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new SeriesEpisodeAdapter(mSelectSeries.getTracks().get(track).getEpisodes()));
        mRecyclerView.requestFocus();

        Glide.with(getApplicationContext()).load(mSelectSeries.getImageUrl()).placeholder(R.drawable.movie_placeholder)
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
        mTitleText.setText(mSelectSeries.getName());
        mEngTitleText.setText(mSelectSeries.getEngName());
        mAudioText.setText(mSelectSeries.getTracks().get(track).getAudio());
        mSubtitleText.setText(mSelectSeries.getTracks().get(track).getSubtitle());

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Subscribe
    public void onSelectEpisodeEvent(SelectEpisodeEvent event) {
        Intent intent = new Intent(this, MoviePlayerActivity.class);
        intent.putExtra(SeriesDetailsActivity.SERIES, Parcels.wrap(mSelectSeries));
        intent.putExtra("url", mSelectSeries.getTracks().get(track).getEpisodes().get(event.position).getUrl());
        intent.putExtra("extra_id", mSelectSeries.getTracks().get(track).getEpisodes().get(event.position).getEpisodeId());
        startActivity(intent);
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
