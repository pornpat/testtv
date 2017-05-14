package com.iptv.iptv.main.data;

import com.iptv.iptv.main.model.SeriesEpisodeItem;
import com.iptv.iptv.main.model.SeriesItem;
import com.iptv.iptv.main.model.SeriesTrackItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karn on 27/4/2560.
 */

public class SeriesDataUtil {

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

    public static List<SeriesItem> getSeriesListFromJson(String responseString) {
        List<SeriesItem> list = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(responseString);

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
                    int order;
                    String videoUrl;
                    List<SeriesEpisodeItem> episodes = new ArrayList<>();
                    JSONArray episodeArray = trackObj.getJSONArray(TAG_EPISODES);
                    for (int k = 0; k < episodeArray.length(); k++) {
                        JSONObject episodeObj = episodeArray.getJSONObject(k);
                        episodeId = episodeObj.getInt("id");
                        order = episodeObj.getInt(TAG_ORDER);
                        JSONArray linkArray = episodeObj.getJSONArray(TAG_LINKS);
                        JSONObject linkObj = linkArray.getJSONObject(0);
                        videoUrl = linkObj.getString(TAG_URL);

                        episodes.add(buildEpisodeInfo(episodeId, order, videoUrl));
                    }

                    tracks.add(buildTrackInfo(trackId, audio, subtitle, episodes));
                }

                list.add(buildSeriesInfo(id, name, description, imageUrl, released, tracks));
            }

            return list;

        } catch (JSONException e) {
            return  list;
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

    private static SeriesEpisodeItem buildEpisodeInfo(int id, int order, String videoUrl) {
        SeriesEpisodeItem episode = new SeriesEpisodeItem();
        episode.setEpisodeId(id);
        episode.setOrder(order);
        episode.setUrl(videoUrl);

        return episode;
    }

}
