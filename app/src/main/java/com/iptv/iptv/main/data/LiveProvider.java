package com.iptv.iptv.main.data;

import android.content.Context;
import android.content.res.Resources;

import com.iptv.iptv.main.model.LiveItem;
import com.iptv.iptv.main.model.LiveProgramItem;

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
    private static final String TAG_MEDIA_ID = "media_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOGOURL = "logo_url";
    private static final String TAG_URL = "url";
    private static final String TAG_PROGRAM = "programs";
    private static final String TAG_START_TIME = "start_time";
    private static final String TAG_END_TIME = "end_time";
    private static final String TAG_HOUR = "hour";
    private static final String TAG_MINUTE = "minute";
    // for history
    private static final String TAG_MEDIA = "media";
    private static final String TAG_MEDIA_TYPE = "media_type";
    private static final String TAG_TYPE_NAME = "type_name";

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
//        if (null != sLiveList) {
//            return sLiveList;
//        }
        sLiveList = new HashMap<>();
        sLiveListById = new HashMap<>();

        JSONArray jsonArray = new LiveProvider().fetchJSON(url);

        if (null == jsonArray) {
            return sLiveList;
        }

        if (url.contains("histories") || url.contains("favorites")) {
            List<LiveItem> liveList = new ArrayList<>();

            int id;
            String name;
            String logoUrl;
            String streamUrl;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                JSONObject media = jsonObj.getJSONObject(TAG_MEDIA);
                JSONObject mediaType = media.getJSONObject(TAG_MEDIA_TYPE);
                String type = mediaType.getString(TAG_TYPE_NAME);

                if (type.equals(TAG_LIVE)) {
                    JSONObject liveObj = media.getJSONObject(TAG_LIVE);
                    id = liveObj.getInt(TAG_MEDIA_ID);
                    name = liveObj.getString(TAG_NAME);
                    logoUrl = liveObj.getString(TAG_LOGOURL);
                    streamUrl = liveObj.getString(TAG_URL);

                    sLiveListById.put(id, buildLiveInfo(id, name, logoUrl, streamUrl, null));
                    liveList.add(buildLiveInfo(id, name, logoUrl, streamUrl, null));
                }
            }

            sLiveList.put("", liveList);

            return sLiveList;
        } else {
            List<LiveItem> liveList = new ArrayList<>();

            int id;
            String name;
            String logoUrl;
            String streamUrl;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                JSONObject liveObj = jsonObj.getJSONObject(TAG_LIVE);
                id = liveObj.getInt(TAG_MEDIA_ID);
                name = liveObj.getString(TAG_NAME);
                logoUrl = liveObj.getString(TAG_LOGOURL);
                streamUrl = liveObj.getString(TAG_URL);

                List<LiveProgramItem> programs = new ArrayList<>();

                JSONArray programArray = liveObj.getJSONArray(TAG_PROGRAM);
                for (int j = 0; j < programArray.length(); j++) {
                    JSONObject program = programArray.getJSONObject(j);
                    String p_name = program.getString(TAG_NAME);
                    JSONObject start = program.getJSONObject(TAG_START_TIME);
                    int startHour = Integer.parseInt(start.getString(TAG_HOUR));
                    int startMin = Integer.parseInt(start.getString(TAG_MINUTE));
                    JSONObject end = program.getJSONObject(TAG_END_TIME);
                    int endHour = Integer.parseInt(end.getString(TAG_HOUR));
                    int endMin = Integer.parseInt(end.getString(TAG_MINUTE));

                    programs.add(new LiveProgramItem(p_name, startHour, startMin, endHour, endMin));
                }

                sLiveListById.put(id, buildLiveInfo(id, name, logoUrl, streamUrl, programs));
                liveList.add(buildLiveInfo(id, name, logoUrl, streamUrl, programs));
            }

            sLiveList.put("", liveList);

            return sLiveList;
        }
    }

    private static LiveItem buildLiveInfo(int id, String name, String logoUrl, String url, List<LiveProgramItem> programs) {
        LiveItem live = new LiveItem();
        live.setId(id);
        live.setName(name);
        live.setLogoUrl(logoUrl);
        live.setUrl(url);
        live.setPrograms(programs);

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
