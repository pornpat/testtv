package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.SelectCategoryEvent;

import org.greenrobot.eventbus.EventBus;

public class MovieGridActivity extends LeanbackActivity {

    ImageView mSearchImage;
    TextView mMovieText;
    TextView mRecentText;
    TextView mFavoriteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mSearchImage = (ImageView) findViewById(R.id.search);
        mMovieText = (TextView) findViewById(R.id.movie);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        mMovieText.requestFocus();
        mMovieText.setSelected(true);

        mSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieGridActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        mMovieText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Movie"));
                setTextSelected(mMovieText);
            }
        });

        mRecentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Recent"));
                setTextSelected(mRecentText);
            }
        });

        mFavoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectCategoryEvent("Favorite"));
                setTextSelected(mFavoriteText);
            }
        });
    }

    private void setTextSelected(TextView currentText) {
        mMovieText.setSelected(false);
        mRecentText.setSelected(false);
        mFavoriteText.setSelected(false);

        currentText.setSelected(true);
    }
}
