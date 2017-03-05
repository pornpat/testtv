package com.iptv.iptv.lib;

import android.app.Activity;
import android.os.Bundle;

import com.iptv.iptv.R;

public class SeriesDetailsActivity extends Activity {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String SERIES = "Series";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_details);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
    }

}
