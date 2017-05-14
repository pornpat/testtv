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
import com.iptv.iptv.R;
import com.iptv.iptv.lib.Utils;
import com.iptv.iptv.main.model.AdsItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    RoundedImageView mAdsImage;
    AdsItem mAdsItem;

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

        mAdsImage = (RoundedImageView) findViewById(R.id.img_advertise);
        mAdsItem = new AdsItem();

        ((TextView) findViewById(R.id.txt_username)).setText(PrefUtil.getStringProperty(R.string.pref_username));
        Log.v("testkn", PrefUtil.getStringProperty(R.string.pref_token));

        mLiveButton.requestFocus();

        Thread myThread;
        Runnable runnable = new CountDownRunner();
        myThread= new Thread(runnable);
        myThread.start();

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
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    mAdsImage.setImageResource(R.drawable.test_advertise);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        mAdsItem.setId(jsonObject.getInt("id"));
                        mAdsItem.setTitle(jsonObject.getString("title"));
                        mAdsItem.setDescription(jsonObject.getString("description"));
                        mAdsItem.setImageUrl(jsonObject.getString("image_url"));

                        Glide.with(HomeActivity.this).load(mAdsItem.getImageUrl()).placeholder(R.drawable.test_advertise)
                                .error(R.drawable.test_advertise).override(400, 200).centerCrop().into(mAdsImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please connect the internet..", Toast.LENGTH_SHORT).show();
        }
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
//                    Date dt = new Date();
//                    int hours = dt.getHours();
//                    int minutes = dt.getMinutes();
//                    int seconds = dt.getSeconds();
//                    String curTime = hours + ":" + minutes + ":" + seconds;

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
    }
}
