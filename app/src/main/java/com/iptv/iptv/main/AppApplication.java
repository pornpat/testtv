package com.iptv.iptv.main;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Karn on 8/4/2560.
 */

public class AppApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getAppContext() {
        return context;
    }

    public static Resources getAppResources() {
        if (context == null) {
            return null;
        }
        return context.getResources();
    }

}
