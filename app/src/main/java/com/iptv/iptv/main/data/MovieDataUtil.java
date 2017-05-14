package com.iptv.iptv.main.data;

import com.iptv.iptv.main.model.DiscItem;
import com.iptv.iptv.main.model.MovieItem;
import com.iptv.iptv.main.model.TrackItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karn on 27/4/2560.
 */

public class MovieDataUtil {

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
    private static final String TAG_DISCS = "discs";
    private static final String TAG_ORDER = "order";
    private static final String TAG_LINKS = "links";
    private static final String TAG_URL = "url";

    private static final String TAG_MEDIA_TYPE = "media_type";
    private static final String TAG_TYPE_NAME = "type_name";

    public static List<MovieItem> getMovieListFromJson(String responseString) {
        List<MovieItem> list = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(responseString);

            int id;
            String name;
            String description;
            String imageUrl;
            String released;
            String type;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObj = jsonArray.getJSONObject(i);

                JSONObject mediaType = movieObj.getJSONObject(TAG_MEDIA_TYPE);
                type = mediaType.getString(TAG_TYPE_NAME);

                JSONObject detailObj = movieObj.getJSONObject(TAG_DETAIL);
                id = detailObj.getInt(TAG_MEDIA_ID);
                name = detailObj.getString(TAG_NAME);
                description = detailObj.getString(TAG_DESCRIPTION);
                imageUrl = detailObj.getString(TAG_IMAGEURL);
                released = detailObj.getString(TAG_RELEASED);

                JSONArray trackArray = movieObj.getJSONArray(TAG_SOUNDTRACK);
                int trackId;
                String subtitle;
                String audio;
                List<TrackItem> tracks = new ArrayList<>();
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

                    int orderId;
                    int discId;
                    String videoUrl;
                    List<DiscItem> discs = new ArrayList<>();
                    JSONArray discArray = trackObj.getJSONArray(TAG_DISCS);
                    for (int k = 0; k < discArray.length(); k++) {
                        JSONObject discObj = discArray.getJSONObject(k);

                        discId = discObj.getInt("id");
                        orderId = discObj.getInt(TAG_ORDER);
                        JSONArray linkArray = discObj.getJSONArray(TAG_LINKS);
                        JSONObject linkObj = linkArray.getJSONObject(0);
                        videoUrl = linkObj.getString(TAG_URL);

                        discs.add(buildDiscInfo(discId, orderId, videoUrl));
                    }

                    tracks.add(buildTrackInfo(trackId, audio, subtitle, discs));
                }

                list.add(buildMovieInfo(id, name, description, imageUrl, released, tracks, type));

            }
            return list;

        } catch (JSONException e) {
            return  list;
        }

    }

    private static MovieItem buildMovieInfo(int id, String name, String description, String imageUrl, String released, List<TrackItem> tracks, String type) {
        MovieItem movie = new MovieItem();
        movie.setId(id);
        movie.setName(name);
        movie.setDescription(description);
        movie.setImageUrl(imageUrl);
        movie.setReleased(released);
        movie.setTracks(tracks);
        movie.setType(type);

        return movie;
    }

    private static TrackItem buildTrackInfo(int id, String audio, String subtitle, List<DiscItem> discs) {
        TrackItem track = new TrackItem();
        track.setId(id);
        track.setAudio(audio);
        track.setSubtitle(subtitle);
        track.setDiscs(discs);

        return track;
    }

    private static DiscItem buildDiscInfo(int id, int orderId, String videoUrl) {
        DiscItem disc = new DiscItem();
        disc.setDiscId(id);
        disc.setId(orderId);
        disc.setVideoUrl(videoUrl);

        return disc;
    }

}
