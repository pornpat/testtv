package com.iptv.iptv.main.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.model.AdsItem;

import org.parceler.Parcels;

public class AdvertiseActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    AdsItem currentAds;
    ImageView mImage;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        currentAds = Parcels.unwrap(getIntent().getParcelableExtra("ads"));

        mImage = (ImageView) findViewById(R.id.img_advertise);
        ((TextView) findViewById(R.id.txt_title)).setText(currentAds.getTitle());
        ((TextView) findViewById(R.id.txt_description)).setText(currentAds.getDescription());
        Glide.with(getApplicationContext()).load(currentAds.getImageUrl()).placeholder(R.drawable.test_advertise).error(R.drawable.test_advertise)
                .override(600, 300).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                findViewById(R.id.content).setVisibility(View.VISIBLE);
                findViewById(R.id.loading).setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                findViewById(R.id.content).setVisibility(View.VISIBLE);
                findViewById(R.id.loading).setVisibility(View.GONE);
                return false;
            }
        }).into(mImage);


        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
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
