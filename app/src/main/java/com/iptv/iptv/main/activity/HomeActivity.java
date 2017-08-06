package com.iptv.iptv.main.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
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
import com.iptv.iptv.main.Utils;
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
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    RelativeLayout mLiveButton;
    RelativeLayout mHitButton;
    RelativeLayout mSportButton;
    RelativeLayout mFavoriteButton;
    RelativeLayout mMovieButton;
    RelativeLayout mSeriesButton;
    RelativeLayout mAdvertiseButton;
    RelativeLayout mVipButton;

    View mSearchButton;
    TextView mDateTimeText;
    TextView mRemainingText;

    RoundedImageView mHitImage1;
    RoundedImageView mHitImage2;
    RoundedImageView mHitImage3;
    RoundedImageView mAdsImage;
    AdsItem mAdsItem;
    List<String> mHitList;
    int currentHit = -1;

    long currentTime;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mLiveButton = (RelativeLayout) findViewById(R.id.btn_live);
        mHitButton = (RelativeLayout) findViewById(R.id.btn_hit);
        mSportButton = (RelativeLayout) findViewById(R.id.btn_sport);
        mFavoriteButton = (RelativeLayout) findViewById(R.id.btn_favorite);
        mMovieButton = (RelativeLayout) findViewById(R.id.btn_movie);
        mSeriesButton = (RelativeLayout) findViewById(R.id.btn_series);
        mAdvertiseButton = (RelativeLayout) findViewById(R.id.btn_advertise);
        mVipButton = (RelativeLayout) findViewById(R.id.btn_vip);

        mSearchButton = findViewById(R.id.btn_search);
        mDateTimeText = (TextView) findViewById(R.id.datetime);
        mDateTimeText.setVisibility(View.INVISIBLE);
        mRemainingText = (TextView) findViewById(R.id.txt_remaining);

        mHitImage1 = (RoundedImageView) findViewById(R.id.img_hit1);
        mHitImage2 = (RoundedImageView) findViewById(R.id.img_hit2);
        mHitImage3 = (RoundedImageView) findViewById(R.id.img_hit3);
        mAdsImage = (RoundedImageView) findViewById(R.id.img_advertise);
        mAdsItem = new AdsItem();
        mHitList = new ArrayList<>();

        ((TextView) findViewById(R.id.txt_username)).setText(PrefUtils.getStringProperty(R.string.pref_username));
        Log.v("testkn", PrefUtils.getStringProperty(R.string.pref_token));

        mLiveButton.requestFocus();

        // FOR ENABLED RUN ON BOOT
        if (!PrefUtils.getBooleanProperty(R.string.pref_default_is_set)) {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                getPackageManager().setComponentEnabledSetting(new ComponentName("com.iptv.iptv", "com.iptv.iptv.main.activity.StartupActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                PrefUtils.setBooleanProperty(R.string.pref_default_on, true);
            }
            PrefUtils.setBooleanProperty(R.string.pref_default_is_set, true);
        }

        // FOR TOUCH ON MOBILE, TABLET
//        mLiveButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    Intent intent = new Intent(HomeActivity.this, LiveGridActivity.class);
//                    startActivity(intent);
//                }
//                return false;
//            }
//        });

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        mLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LiveGridActivity.class);
                startActivity(intent);
            }
        });

        mMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MovieGridActivity.class);
                startActivity(intent);
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

        mVipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, VipPasswordActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_remaining).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, TopupActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UserSettingActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUserProfile() {
        if (Utils.isInternetConnectionAvailable(this)) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(ApiUtils.appendUri(ApiUtils.ALL_PACKAGE_URL, ApiUtils.addToken()), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    PrefUtils.setStringProperty(R.string.pref_token, "");
                    Toast.makeText(HomeActivity.this, "Token หมดอายุ กรุณาล็อกอินใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseString);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int remain_day = jsonObject.getInt("remaining_days");

                            mRemainingText.setText(remain_day + " วัน");
                        } else {
                            mRemainingText.setText("0 วัน");
                        }

//                        //format yyyy-mm-dd hh:nn:ss
//                        int year = Integer.parseInt(expire.substring(0, 4));
//                        int month = Integer.parseInt(expire.substring(5, 7));
//                        int date = Integer.parseInt(expire.substring(8, 10));
//
//                        Calendar expireDate = Calendar.getInstance();
//                        expireDate.set(year, month, date);
//
//                        long diff = expireDate.getTimeInMillis() - System.currentTimeMillis();
//                        long diffDays = diff / (24 * 60 * 60 * 1000);
//
//                        Log.v("testkn", diffDays + "");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please connect the internet..", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAdvertise() {
        if (Utils.isInternetConnectionAvailable(this)) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(ApiUtils.appendUri(ApiUtils.ADVERTISE_URL, ApiUtils.addToken()), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers,
                        String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                        String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        mAdsItem.setId(jsonObject.getInt("id"));
                        mAdsItem.setTitle(jsonObject.getString("title"));
                        mAdsItem.setDescription(jsonObject.getString("description"));
                        mAdsItem.setImageUrl(jsonObject.getString("image_url"));

                        Glide.with(getApplicationContext()).load(mAdsItem.getImageUrl()).override(400, 200).centerCrop()
                                .error(R.drawable.test_advertise).listener(
                                new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model,
                                            Target<GlideDrawable> target,
                                            boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource,
                                            String model, Target<GlideDrawable> target,
                                            boolean isFromMemoryCache,
                                            boolean isFirstResource) {
                                        mAdsImage.setImageDrawable(resource);
                                        mAdsImage.setCornerRadiusDimen(
                                                R.dimen.margin_padding_small);
                                        return true;
                                    }
                                }).into(mAdsImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void fetchTime() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.appendUri(ApiUtils.TIME_URL, ApiUtils.addToken()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            currentTime = jsonObject.getLong("timestamp") * 1000L;

                            Thread myThread;
                            Runnable runnable = new CountDownRunner();
                            myThread= new Thread(runnable);
                            myThread.start();

                            mDateTimeText.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void fetchNotice() {
        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.get(ApiUtils.appendUri(ApiUtils.NOTICE_URL, ApiUtils.addToken()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                                          Throwable throwable) {}

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            if (jsonObject.has("message")) {
                                ((TextView) findViewById(R.id.notice)).setText(jsonObject.getString("message"));
                                findViewById(R.id.notice).setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void fetchHitMovie() {
        if (Utils.isInternetConnectionAvailable(this)) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(ApiUtils.appendUri(ApiUtils.TOP_10_HIT_URL, ApiUtils.addToken()), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseString);
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
        shuffleCurrentHit();
        Glide.with(getApplicationContext()).load(mHitList.get(currentHit)).override(150, 200).centerCrop().error(R.drawable.movie_placeholder)
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

        shuffleCurrentHit();
        Glide.with(getApplicationContext()).load(mHitList.get(currentHit)).override(150, 200).centerCrop().error(R.drawable.movie_placeholder)
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

        shuffleCurrentHit();
        Glide.with(getApplicationContext()).load(mHitList.get(currentHit)).override(150, 200).centerCrop().error(R.drawable.movie_placeholder)
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

    private void shuffleCurrentHit() {
        if (currentHit < mHitList.size() - 1) {
            currentHit++;
        } else {
            currentHit = 0;
        }
    }

    private void fetchAllComponents() {
        updateUserProfile();
        updateAdvertise();
        fetchTime();
        fetchNotice();
        if (mHitList.size() > 0) {
            updateHitMovie();
        } else {
            fetchHitMovie();
        }
    }

    @Override
    public void networkAvailable() {
        fetchAllComponents();
    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "Network unavailable.. Please check your wifi-connection", Toast.LENGTH_LONG).show();
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String formattedDate = df.format(new Date(currentTime));

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
                    currentTime += 1000L;
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
        if (Utils.isInternetConnectionAvailable(this)) {
            fetchAllComponents();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }
}
