package com.iptv.iptv.main;

import android.content.Context;
import android.content.SharedPreferences;

import com.iptv.iptv.R;

/**
 * Created by Karn on 8/4/2560.
 */

public class PrefUtil {

    private static SharedPreferences getPref() {
        return AppApplication.getAppContext().getSharedPreferences(AppApplication.getAppResources().getString(R.string.preference_file), Context.MODE_PRIVATE);
    }

    public static String getStringProperty(int resId) {
        return getPref().getString(AppApplication.getAppResources().getString(resId), "");
    }

    public static void setStringProperty(int resId, String value) {
        getPref().edit().putString(AppApplication.getAppResources().getString(resId), value).commit();
    }

    public static boolean getBooleanProperty(int resId) {
        return getPref().getBoolean(AppApplication.getAppResources().getString(resId), false);
    }

    public static void setBooleanProperty(int resId, boolean value) {
        getPref().edit().putBoolean(AppApplication.getAppResources().getString(resId), value).commit();
    }

}
