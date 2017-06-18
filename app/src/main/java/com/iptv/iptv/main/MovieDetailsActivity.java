/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.iptv.iptv.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iptv.iptv.R;
import com.iptv.iptv.main.data.MovieDataUtil;
import com.iptv.iptv.main.model.MovieItem;
import com.iptv.iptv.main.model.TrackItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MovieDetailsActivity extends Activity {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String MOVIE = "Movie";

    private MovieItem mSelectedMovie;

    private ImageView mImage;
    private TextView mEngTitle;
    private TextView mTitle;
    private TextView mDetail;
    private RecyclerView mChoiceList;
    private RecyclerView mRecommendList;

    private View mLoading;
    private View mContent;

    private boolean isFav = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mSelectedMovie = Parcels.unwrap(getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE));

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
        client.get(UrlUtil.appendUri(UrlUtil.getFavCheckUrl(mSelectedMovie.getId()), UrlUtil.addToken()), new TextHttpResponseHandler() {
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

        if (mSelectedMovie.getType().equals("movie")) {
            AsyncHttpClient client2 = new AsyncHttpClient();
            client2.get(UrlUtil.appendUri(UrlUtil.getRecommendUrl(UrlUtil.MOVIE_URL, mSelectedMovie.getId()), UrlUtil.addToken()), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    List<MovieItem> list = MovieDataUtil.getMovieListFromJson(responseString);
                    mRecommendList.setAdapter(new RecommendAdapter(MovieDetailsActivity.this, list));
                }
            });
        } else if (mSelectedMovie.getType().equals("sport")) {
            AsyncHttpClient client2 = new AsyncHttpClient();
            client2.get(UrlUtil.appendUri(UrlUtil.getRecommendUrl(UrlUtil.SPORT_URL, mSelectedMovie.getId()), UrlUtil.addToken()), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    List<MovieItem> list = MovieDataUtil.getMovieListFromJson(responseString);
                    mRecommendList.setAdapter(new RecommendAdapter(MovieDetailsActivity.this, list));
                }
            });
        }
    }

    private void showChoice() {
        List<TrackItem> trackList = mSelectedMovie.getTracks();
        trackList.add(new TrackItem());
        mChoiceList.setAdapter(new ChoiceAdapter(MovieDetailsActivity.this, trackList, isFav));
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

}
