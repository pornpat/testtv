package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.LoadMovieEvent;
import com.iptv.iptv.main.event.SelectCategoryEvent;
import com.iptv.iptv.main.model.CategoryItem;

import org.greenrobot.eventbus.EventBus;

public class MovieGridActivity extends LeanbackActivity implements FilterFragment.OnListFragmentInteractionListener {

    TextView mMovieText;
    TextView mRecentText;
    TextView mFavoriteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mMovieText = (TextView) findViewById(R.id.movie);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadMovieEvent("http://139.59.231.135/uplay/public/api/v1/movies"));
            }
        }, 500);

        mMovieText.requestFocus();
        mMovieText.setSelected(true);

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
                getFragmentManager().beginTransaction().replace(R.id.layout_filter, new FilterFragment()).commit();
                findViewById(R.id.layout_filter).setVisibility(View.VISIBLE);

//                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.layout_filter)).commit();
//                findViewById(R.id.layout_filter).setVisibility(View.GONE);
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

    @Override
    public void onListFragmentInteraction(CategoryItem item) {
        Log.v("testkn", item.getName());
    }
}
