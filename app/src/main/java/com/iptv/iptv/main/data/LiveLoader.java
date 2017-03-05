package com.iptv.iptv.main.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.iptv.iptv.main.model.LiveItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Karn on 5/3/2560.
 */

public class LiveLoader extends AsyncTaskLoader<HashMap<String, List<LiveItem>>> {

    private static final String TAG = "LiveLoader";
    private final String mUrl;

    public LiveLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public HashMap<String, List<LiveItem>> loadInBackground() {
        try {
            return LiveProvider.buildMedia(mUrl);
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
