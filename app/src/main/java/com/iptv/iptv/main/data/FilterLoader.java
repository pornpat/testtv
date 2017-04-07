package com.iptv.iptv.main.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.iptv.iptv.main.model.CategoryItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Asus N46V on 26/3/2017.
 */

public class FilterLoader extends AsyncTaskLoader<HashMap<String, List<CategoryItem>>> {

    private static final String TAG = "FilterLoader";
    private final String mUrl;

    public FilterLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public HashMap<String, List<CategoryItem>> loadInBackground() {
        try {
            return FilterProvider.buildMedia(mUrl);
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
