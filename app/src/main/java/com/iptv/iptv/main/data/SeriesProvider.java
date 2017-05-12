package com.iptv.iptv.main.data;

import android.content.Context;
import android.content.res.Resources;

import com.iptv.iptv.main.model.SeriesEpisodeItem;
import com.iptv.iptv.main.model.SeriesItem;
import com.iptv.iptv.main.model.SeriesTrackItem;

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

public class SeriesProvider {

    private static final String TAG_MEDIA_ID = "media_id";
    private static final String TAG_DETAIL = "detail";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_IMAGEURL = "image_url";
    private static final String TAG_RELEASED = "released";
    private static final String TAG_SOUNDTRACK = "soundtracks";
    private static final String TAG_AUDIO_ID = "audio_id";
    private static final String TAG_SUBTlTLE = "subtitle";
    private static final String TAG_AUDIO = "audio";
    private static final String TAG_LANGUAGE = "language";
    private static final String TAG_EPISODES = "episodes";
    private static final String TAG_ORDER = "order";
    private static final String TAG_LINKS = "links";
    private static final String TAG_URL = "url";
    // for history
    private static final String TAG_MEDIA = "media";
    private static final String TAG_MEDIA_TYPE = "media_type";
    private static final String TAG_TYPE_NAME = "type_name";
    private static final String TAG_SERIES = "series";

    private static HashMap<String, List<SeriesItem>> sSeriesList;
    private static HashMap<Integer, SeriesItem> sSeriesListById;

    private static Resources sResources;

    public static void setContext(Context context) {
        if (null == sResources) {
            sResources = context.getResources();
        }
    }

    public static SeriesItem getSeriesById(String mediaId) {
        return sSeriesListById.get(mediaId);
    }

    public static HashMap<String, List<SeriesItem>> getMovieList() {
        return sSeriesList;
    }

    public static HashMap<String, List<SeriesItem>> buildMedia(String url) throws JSONException {
//        if (null != sSeriesList) {
//            return sSeriesList;
//        }
        sSeriesList = new HashMap<>();
        sSeriesListById = new HashMap<>();

        JSONObject jsonObject = new SeriesProvider().fetchJSON(url);
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        if (null == jsonArray) {
            return sSeriesList;
        }

        if (url.contains("histories") || url.contains("favorites")) {
            List<SeriesItem> seriesList = new ArrayList<>();

            int id;
            String name;
            String description;
            String imageUrl;
            String released;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject seriesObj = jsonArray.getJSONObject(i);

                JSONObject media = seriesObj.getJSONObject(TAG_MEDIA);
                JSONObject mediaType = media.getJSONObject(TAG_MEDIA_TYPE);
                String type = mediaType.getString(TAG_TYPE_NAME);

                if (type.equals(TAG_SERIES)) {

                    JSONObject detailObj = media.getJSONObject(TAG_DETAIL);
                    id = detailObj.getInt(TAG_MEDIA_ID);
                    name = detailObj.getString(TAG_NAME);
                    description = detailObj.getString(TAG_DESCRIPTION);
                    imageUrl = detailObj.getString(TAG_IMAGEURL);
                    released = detailObj.getString(TAG_RELEASED);

                    JSONArray trackArray = media.getJSONArray(TAG_SOUNDTRACK);
                    int trackId;
                    String subtitle;
                    String audio;
                    List<SeriesTrackItem> tracks = new ArrayList<>();
                    for (int j = 0; j < trackArray.length(); j++) {
                        JSONObject trackObj = trackArray.getJSONObject(j);
                        trackId = trackObj.getInt(TAG_AUDIO_ID);
                        if (trackObj.isNull(TAG_SUBTlTLE)) {
                            subtitle = "-";
                        } else {
                            JSONObject subtitleObj = trackObj.getJSONObject(TAG_SUBTlTLE);
                            subtitle = subtitleObj.getString(TAG_LANGUAGE);
                        }

                        JSONObject audioObj = trackObj.getJSONObject(TAG_AUDIO);
                        audio = audioObj.getString(TAG_LANGUAGE);

                        int episodeId;
                        String videoUrl;
                        List<SeriesEpisodeItem> episodes = new ArrayList<>();
                        JSONArray episodeArray = trackObj.getJSONArray(TAG_EPISODES);
                        for (int k = 0; k < episodeArray.length(); k++) {
                            JSONObject episodeObj = episodeArray.getJSONObject(k);
                            episodeId = episodeObj.getInt(TAG_ORDER);
                            JSONArray linkArray = episodeObj.getJSONArray(TAG_LINKS);
                            JSONObject linkObj = linkArray.getJSONObject(0);
                            videoUrl = linkObj.getString(TAG_URL);

                            episodes.add(buildEpisodeInfo(episodeId, videoUrl));
                        }

                        tracks.add(buildTrackInfo(trackId, audio, subtitle, episodes));
                    }

                    sSeriesListById.put(id, buildSeriesInfo(id, name, description, imageUrl, released, tracks));
                    seriesList.add(buildSeriesInfo(id, name, description, imageUrl, released, tracks));
                }
            }

            sSeriesList.put("", seriesList);

            return sSeriesList;
        } else {
            List<SeriesItem> seriesList = new ArrayList<>();

            int id;
            String name;
            String description;
            String imageUrl;
            String released;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject seriesObj = jsonArray.getJSONObject(i);

                JSONObject detailObj = seriesObj.getJSONObject(TAG_DETAIL);
                id = detailObj.getInt(TAG_MEDIA_ID);
                name = detailObj.getString(TAG_NAME);
                description = detailObj.getString(TAG_DESCRIPTION);
                imageUrl = detailObj.getString(TAG_IMAGEURL);
                released = detailObj.getString(TAG_RELEASED);

                JSONArray trackArray = seriesObj.getJSONArray(TAG_SOUNDTRACK);
                int trackId;
                String subtitle;
                String audio;
                List<SeriesTrackItem> tracks = new ArrayList<>();
                for (int j = 0; j < trackArray.length(); j++) {
                    JSONObject trackObj = trackArray.getJSONObject(j);
                    trackId = trackObj.getInt(TAG_AUDIO_ID);
                    if (trackObj.isNull(TAG_SUBTlTLE)) {
                        subtitle = "-";
                    } else {
                        JSONObject subtitleObj = trackObj.getJSONObject(TAG_SUBTlTLE);
                        subtitle = subtitleObj.getString(TAG_LANGUAGE);
                    }

                    JSONObject audioObj = trackObj.getJSONObject(TAG_AUDIO);
                    audio = audioObj.getString(TAG_LANGUAGE);

                    int episodeId;
                    String videoUrl;
                    List<SeriesEpisodeItem> episodes = new ArrayList<>();
                    JSONArray episodeArray = trackObj.getJSONArray(TAG_EPISODES);
                    for (int k = 0; k < episodeArray.length(); k++) {
                        JSONObject episodeObj = episodeArray.getJSONObject(k);
                        episodeId = episodeObj.getInt(TAG_ORDER);
                        JSONArray linkArray = episodeObj.getJSONArray(TAG_LINKS);
                        JSONObject linkObj = linkArray.getJSONObject(0);
                        videoUrl = linkObj.getString(TAG_URL);

                        episodes.add(buildEpisodeInfo(episodeId, videoUrl));
                    }

                    tracks.add(buildTrackInfo(trackId, audio, subtitle, episodes));
                }

                sSeriesListById.put(id, buildSeriesInfo(id, name, description, imageUrl, released, tracks));
                seriesList.add(buildSeriesInfo(id, name, description, imageUrl, released, tracks));
            }

            sSeriesList.put("", seriesList);

            return sSeriesList;
        }
    }

    private static SeriesItem buildSeriesInfo(int id, String name, String description, String imageUrl, String released, List<SeriesTrackItem> tracks) {
        SeriesItem series = new SeriesItem();
        series.setId(id);
        series.setName(name);
        series.setDescription(description);
        series.setImageUrl(imageUrl);
        series.setReleased(released);
        series.setTracks(tracks);

        return series;
    }

    private static SeriesTrackItem buildTrackInfo(int id, String audio, String subtitle, List<SeriesEpisodeItem> episodes) {
        SeriesTrackItem track = new SeriesTrackItem();
        track.setId(id);
        track.setAudio(audio);
        track.setSubtitle(subtitle);
        track.setEpisodes(episodes);

        return track;
    }

    private static SeriesEpisodeItem buildEpisodeInfo(int order, String videoUrl) {
        SeriesEpisodeItem episode = new SeriesEpisodeItem();
        episode.setOrder(order);
        episode.setUrl(videoUrl);

        return episode;
    }

    private JSONObject fetchJSON(String urlString) {
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
            return new JSONObject(json);
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
