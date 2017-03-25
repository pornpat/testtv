package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iptv.iptv.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends LeanbackActivity {

    RelativeLayout mLiveButton;
    RelativeLayout mRecommendButton;
    RelativeLayout mSportButton;
    RelativeLayout mFavouriteButton;
    RelativeLayout mMovieButton;
    RelativeLayout mSeriesButton;
    RelativeLayout mAdvertiseButton;
    RelativeLayout mSettingButton;

    TextView mDateTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mLiveButton = (RelativeLayout) findViewById(R.id.btn_live);
        mRecommendButton = (RelativeLayout) findViewById(R.id.btn_recommend);
        mSportButton = (RelativeLayout) findViewById(R.id.btn_sport);
        mFavouriteButton = (RelativeLayout) findViewById(R.id.btn_favourite);
        mMovieButton = (RelativeLayout) findViewById(R.id.btn_movie);
        mSeriesButton = (RelativeLayout) findViewById(R.id.btn_series);
        mAdvertiseButton = (RelativeLayout) findViewById(R.id.btn_advertise);
        mSettingButton = (RelativeLayout) findViewById(R.id.btn_setting);

        mDateTimeText = (TextView) findViewById(R.id.datetime);

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

}
