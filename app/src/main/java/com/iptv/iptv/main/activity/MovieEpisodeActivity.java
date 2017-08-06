package com.iptv.iptv.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iptv.iptv.R;
import com.iptv.iptv.main.MovieEpisodeAdapter;
import com.iptv.iptv.main.event.SelectEpisodeEvent;
import com.iptv.iptv.main.model.MovieItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

public class MovieEpisodeActivity extends AppCompatActivity {

    private static MovieItem mSelectMovie;
    private static int track;

    RecyclerView mRecyclerView;
    ImageView mImage;
    TextView mTitleText;
    TextView mEngTitleText;
    TextView mAudioText;
    TextView mSubtitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSelectMovie = Parcels.unwrap(getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE));
        track = getIntent().getExtras().getInt("track");

        mImage = (ImageView) findViewById(R.id.img);
        mTitleText = (TextView) findViewById(R.id.txt_title);
        mEngTitleText = (TextView) findViewById(R.id.txt_eng_title);
        mAudioText = (TextView) findViewById(R.id.txt_audio);
        mSubtitleText = (TextView) findViewById(R.id.txt_subtitle);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new MovieEpisodeAdapter(mSelectMovie.getTracks().get(track).getDiscs()));
        mRecyclerView.requestFocus();

        Glide.with(getApplicationContext()).load(mSelectMovie.getImageUrl()).placeholder(R.drawable.movie_placeholder)
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
        mTitleText.setText(mSelectMovie.getName());
        mEngTitleText.setText(mSelectMovie.getEngName());
        mAudioText.setText(mSelectMovie.getTracks().get(track).getAudio());
        mSubtitleText.setText(mSelectMovie.getTracks().get(track).getSubtitle());


    }

    @Subscribe
    public void onSelectEpisodeEvent(SelectEpisodeEvent event) {
        Intent intent = new Intent(this, MoviePlayerActivity.class);
        intent.putExtra(MovieDetailsActivity.MOVIE, Parcels.wrap(mSelectMovie));
        intent.putExtra("url", mSelectMovie.getTracks().get(track).getDiscs().get(event.position).getVideoUrl());
        intent.putExtra("extra_id", mSelectMovie.getTracks().get(track).getDiscs().get(event.position).getDiscId());
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
}
