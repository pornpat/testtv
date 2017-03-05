package com.iptv.iptv.main.data;

import android.content.Context;
import android.content.res.Resources;

import com.iptv.iptv.main.model.LiveItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Karn on 5/3/2560.
 */

public class LiveProvider {

    private static final String TAG_LIVE = "live";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOGOURL = "logo_url";
    private static final String TAG_URL = "url";

    private static HashMap<String, List<LiveItem>> sLiveList;
    private static HashMap<Integer, LiveItem> sLiveListById;

    private static Resources sResources;

    public static void setContext(Context context) {
        if (null == sResources) {
            sResources = context.getResources();
        }
    }

    public static LiveItem getLiveById(String mediaId) {
        return sLiveListById.get(mediaId);
    }

    public static HashMap<String, List<LiveItem>> getLiveList() {
        return sLiveList;
    }

    public static HashMap<String, List<LiveItem>> buildMedia(String url) throws JSONException {
        if (null != sLiveList) {
            return sLiveList;
        }
        sLiveList = new HashMap<>();
        sLiveListById = new HashMap<>();

        JSONArray jsonArray = new LiveProvider().fetchJSON(url);

        if (null == jsonArray) {
            return sLiveList;
        }

        List<LiveItem> liveList = new ArrayList<>();

        int id;
        String name;
        String logoUrl;
        String streamUrl;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);

            JSONObject liveObj = jsonObj.getJSONObject(TAG_LIVE);
            id = liveObj.getInt(TAG_ID);
            name = liveObj.getString(TAG_NAME);
            logoUrl = liveObj.getString(TAG_LOGOURL);
            streamUrl = liveObj.getString(TAG_URL);

            sLiveListById.put(id, buildLiveInfo(id, name, logoUrl, streamUrl));
            liveList.add(buildLiveInfo(id, name, logoUrl, streamUrl));
        }

        sLiveList.put("", liveList);

        return sLiveList;
    }

    private static LiveItem buildLiveInfo(int id, String name, String logoUrl, String url) {
        LiveItem live = new LiveItem();
        live.setId(id);
        live.setName(name);
        live.setLogoUrl(logoUrl);
        live.setUrl(url);

        return live;
    }

    private JSONArray fetchJSON(String urlString) {
        BufferedReader reader = null;

        try {
            java.net.URL url = new java.net.URL(urlString);
            URLConnection urlConnection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "iso-8859-1"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return new JSONArray(json);
        } catch (Exception e) {
            return null;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
