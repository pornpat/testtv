package com.iptv.iptv.main.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.iptv.iptv.main.model.LiveItem;
import com.iptv.iptv.main.model.SeriesItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Karn on 5/3/2560.
 */

public class SeriesLoader extends AsyncTaskLoader<HashMap<String, List<SeriesItem>>> {

    private static final String TAG = "SeriesLoader";
    private final String mUrl;

    public SeriesLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public HashMap<String, List<SeriesItem>> loadInBackground() {
        try {
            return SeriesProvider.buildMedia(mUrl);
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch media data", e);
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

}
