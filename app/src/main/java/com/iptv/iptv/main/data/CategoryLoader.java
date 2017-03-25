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

public class CategoryLoader extends AsyncTaskLoader<HashMap<String, List<CategoryItem>>> {

    private static final String TAG = "CategoryLoader";
    private final String mUrl;

    public CategoryLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public HashMap<String, List<CategoryItem>> loadInBackground() {
        try {
            return CategoryProvider.buildMedia(mUrl);
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
