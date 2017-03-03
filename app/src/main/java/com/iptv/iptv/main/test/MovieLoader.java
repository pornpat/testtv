package com.iptv.iptv.main.test;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Asus N46V on 3/3/2017.
 */

public class MovieLoader extends AsyncTaskLoader<HashMap<String, List<MovieItem>>> {

    private static final String TAG = "MovieLoader";
    private final String mUrl;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public HashMap<String, List<MovieItem>> loadInBackground() {
        try {
            return MovieProvider.buildMedia(mUrl);
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
