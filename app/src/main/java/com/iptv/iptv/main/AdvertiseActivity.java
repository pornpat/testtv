package com.iptv.iptv.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iptv.iptv.R;
import com.iptv.iptv.main.model.AdsItem;

import org.parceler.Parcels;

public class AdvertiseActivity extends LeanbackActivity {

    AdsItem currentAds;
    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);

        currentAds = Parcels.unwrap(getIntent().getParcelableExtra("ads"));

        mImage = (ImageView) findViewById(R.id.img_advertise);
        ((TextView) findViewById(R.id.txt_title)).setText(currentAds.getTitle());
        ((TextView) findViewById(R.id.txt_description)).setText(currentAds.getDescription());
        Glide.with(this).load(currentAds.getImageUrl()).listener(new RequestListener<String, GlideDrawable>() {
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

    }
}
