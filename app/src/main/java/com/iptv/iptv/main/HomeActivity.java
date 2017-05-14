package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iptv.iptv.R;
import com.iptv.iptv.lib.Utils;
import com.iptv.iptv.main.model.AdsItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.makeramen.roundedimageview.Corner;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends LeanbackActivity {

    RelativeLayout mLiveButton;
    RelativeLayout mHitButton;
    RelativeLayout mSportButton;
    RelativeLayout mFavoriteButton;
    RelativeLayout mMovieButton;
    RelativeLayout mSeriesButton;
    RelativeLayout mAdvertiseButton;
    RelativeLayout mSettingButton;

    View mSearchButton;
    TextView mDateTimeText;

    RoundedImageView mHitImage1;
    RoundedImageView mHitImage2;
    RoundedImageView mHitImage3;
    RoundedImageView mAdsImage;
    AdsItem mAdsItem;
    List<String> mHitList;
    int currentSet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mLiveButton = (RelativeLayout) findViewById(R.id.btn_live);
        mHitButton = (RelativeLayout) findViewById(R.id.btn_hit);
        mSportButton = (RelativeLayout) findViewById(R.id.btn_sport);
        mFavoriteButton = (RelativeLayout) findViewById(R.id.btn_favorite);
        mMovieButton = (RelativeLayout) findViewById(R.id.btn_movie);
        mSeriesButton = (RelativeLayout) findViewById(R.id.btn_series);
        mAdvertiseButton = (RelativeLayout) findViewById(R.id.btn_advertise);
        mSettingButton = (RelativeLayout) findViewById(R.id.btn_setting);

        mSearchButton = findViewById(R.id.btn_search);
        mDateTimeText = (TextView) findViewById(R.id.datetime);

        mHitImage1 = (RoundedImageView) findViewById(R.id.img_hit1);
        mHitImage2 = (RoundedImageView) findViewById(R.id.img_hit2);
        mHitImage3 = (RoundedImageView) findViewById(R.id.img_hit3);
        mAdsImage = (RoundedImageView) findViewById(R.id.img_advertise);
        mAdsItem = new AdsItem();
        mHitList = new ArrayList<>();

        ((TextView) findViewById(R.id.txt_username)).setText(PrefUtil.getStringProperty(R.string.pref_username));
        Log.v("testkn", PrefUtil.getStringProperty(R.string.pref_token));

        mLiveButton.requestFocus();

        Thread myThread;
        Runnable runnable = new CountDownRunner();
        myThread= new Thread(runnable);
        myThread.start();

        fetchHitMovie();

        mLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LiveGridActivity.class);
                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this)
                                .toBundle();
                startActivity(intent, bundle);
            }
        });

        mMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MovieGridActivity.class);
                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this)
                                .toBundle();
                startActivity(intent, bundle);
            }
        });

        mSeriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SeriesGridActivity.class);
                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this)
                                .toBundle();
                startActivity(intent, bundle);
            }
        });

        mSportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SportGridActivity.class);
                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this)
                                .toBundle();
                startActivity(intent, bundle);
            }
        });

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FavoriteGridActivity.class);
                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this)
                                .toBundle();
                startActivity(intent, bundle);
            }
        });

        mHitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, HitGridActivity.class);
                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this)
                                .toBundle();
                startActivity(intent, bundle);
            }
        });

        mAdvertiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AdvertiseActivity.class);
                intent.putExtra("ads", Parcels.wrap(mAdsItem));
                startActivity(intent);
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this)
                                .toBundle();
                startActivity(intent, bundle);
            }
        });
    }

    private void updateAdvertise() {
        if (Utils.isInternetConnectionAvailable(this)) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(UrlUtil.appendUri(UrlUtil.ADVERTISE_URL, UrlUtil.addToken()), new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        mAdsItem.setId(jsonObject.getInt("id"));
                        mAdsItem.setTitle(jsonObject.getString("title"));
                        mAdsItem.setDescription(jsonObject.getString("description"));
                        mAdsItem.setImageUrl(jsonObject.getString("image_url"));

                        Glide.with(HomeActivity.this).load(mAdsItem.getImageUrl()).override(400, 200).centerCrop()
                                .error(R.drawable.test_advertise).listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mAdsImage.setImageDrawable(resource);
                                mAdsImage.setCornerRadiusDimen(R.dimen.margin_padding_small);
                                return true;
                            }
                        }).into(mAdsImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please connect the internet..", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchHitMovie() {
        if (Utils.isInternetConnectionAvailable(this)) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(UrlUtil.appendUri(UrlUtil.MOVIE_HIT_URL, UrlUtil.addToken()), new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject movieItem = jsonArray.getJSONObject(i);
                            JSONObject movieDetail = movieItem.getJSONObject("detail");

                            mHitList.add(movieDetail.getString("image_url"));
                        }
                        updateHitMovie();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void updateHitMovie() {
        int img1 = 0, img2 = 1, img3 = 2;
        if (mHitList.size() > 3) {
            if (currentSet == 0) {
                img1 = 0; img2 = 1; img3 = 2;
                currentSet = 1;
            } else if (currentSet == 1) {
                img1 = 3; img2 = 4; img3 = 5;
                currentSet = 2;
            } else {
                img1 = 6; img2 = 7; img3 = 8;
                currentSet = 0;
            }
        }
        Glide.with(HomeActivity.this).load(mHitList.get(img1)).override(150, 200).centerCrop().error(R.drawable.movie_placeholder)
                .listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mHitImage1.setImageDrawable(resource);
                mHitImage1.setCornerRadiusDimen(Corner.TOP_LEFT, R.dimen.margin_padding_small);
                mHitImage1.setCornerRadiusDimen(Corner.BOTTOM_LEFT, R.dimen.margin_padding_small);
                return true;
            }
        }).into(mHitImage1);

        Glide.with(HomeActivity.this).load(mHitList.get(img2)).override(150, 200).centerCrop().error(R.drawable.movie_placeholder)
                .listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mHitImage2.setImageDrawable(resource);
                return true;
            }
        }).into(mHitImage2);

        Glide.with(HomeActivity.this).load(mHitList.get(img3)).override(150, 200).centerCrop().error(R.drawable.movie_placeholder)
                .listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mHitImage3.setImageDrawable(resource);
                mHitImage3.setCornerRadiusDimen(Corner.TOP_RIGHT, R.dimen.margin_padding_small);
                mHitImage3.setCornerRadiusDimen(Corner.BOTTOM_RIGHT, R.dimen.margin_padding_small);
                return true;
            }
        }).into(mHitImage3);
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    mDateTimeText.setText(formattedDate);
                }catch (Exception e) {}
            }
        });
    }


    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdvertise();
        if (mHitList.size() > 0) {
            updateHitMovie();
        }
    }
}
