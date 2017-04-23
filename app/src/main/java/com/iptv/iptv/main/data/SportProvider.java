package com.iptv.iptv.main.data;

import android.content.Context;
import android.content.res.Resources;

import com.iptv.iptv.main.model.DiscItem;
import com.iptv.iptv.main.model.MovieItem;
import com.iptv.iptv.main.model.TrackItem;

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
 * Created by Asus N46V on 3/3/2017.
 */

public class SportProvider {

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
    // for history
    private static final String TAG_MEDIA = "media";
    private static final String TAG_MEDIA_TYPE = "media_type";
    private static final String TAG_TYPE_NAME = "type_name";
    private static final String TAG_SPORT = "sport";

    private static HashMap<String, List<MovieItem>> sMovieList;
    private static HashMap<Integer, MovieItem> sMovieListById;

    private static Resources sResources;

    public static void setContext(Context context) {
        if (null == sResources) {
            sResources = context.getResources();
        }
    }

    public static MovieItem getMovieById(String mediaId) {
        return sMovieListById.get(mediaId);
    }

    public static HashMap<String, List<MovieItem>> getMovieList() {
        return sMovieList;
    }

    public static HashMap<String, List<MovieItem>> buildMedia(String url) throws JSONException {
//        if (null != sMovieList) {
//            return sMovieList;
//        }
        sMovieList = new HashMap<>();
        sMovieListById = new HashMap<>();

        JSONArray jsonArray = new SportProvider().fetchJSON(url);

        if (null == jsonArray) {
            return sMovieList;
        }

        if (url.contains("histories") || url.contains("favorites")) {
            List<MovieItem> movieList = new ArrayList<>();

            int id;
            String name;
            String description;
            String imageUrl;
            String released;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObj = jsonArray.getJSONObject(i);

                JSONObject media = movieObj.getJSONObject(TAG_MEDIA);
                JSONObject mediaType = media.getJSONObject(TAG_MEDIA_TYPE);
                String type = mediaType.getString(TAG_TYPE_NAME);

                if (type.equals(TAG_SPORT)) {
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

                        int discId;
                        String videoUrl;
                        List<DiscItem> discs = new ArrayList<>();
                        JSONArray discArray = trackObj.getJSONArray(TAG_DISCS);
                        for (int k = 0; k < discArray.length(); k++) {
                            JSONObject discObj = discArray.getJSONObject(k);
                            discId = discObj.getInt(TAG_ORDER);
                            JSONArray linkArray = discObj.getJSONArray(TAG_LINKS);
                            JSONObject linkObj = linkArray.getJSONObject(0);
                            videoUrl = linkObj.getString(TAG_URL);

                            discs.add(buildDiscInfo(discId, videoUrl));
                        }

                        tracks.add(buildTrackInfo(trackId, audio, subtitle, discs));
                    }

                    sMovieListById.put(id, buildMovieInfo(id, name, description, imageUrl, released, tracks));
                    movieList.add(buildMovieInfo(id, name, description, imageUrl, released, tracks));
                }
            }

            sMovieList.put("", movieList);

            return sMovieList;
        } else {
            List<MovieItem> movieList = new ArrayList<>();

            int id;
            String name;
            String description;
            String imageUrl;
            String released;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObj = jsonArray.getJSONObject(i);

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

                    int discId;
                    String videoUrl;
                    List<DiscItem> discs = new ArrayList<>();
                    JSONArray discArray = trackObj.getJSONArray(TAG_DISCS);
                    for (int k = 0; k < discArray.length(); k++) {
                        JSONObject discObj = discArray.getJSONObject(k);
                        discId = discObj.getInt(TAG_ORDER);
                        JSONArray linkArray = discObj.getJSONArray(TAG_LINKS);
                        JSONObject linkObj = linkArray.getJSONObject(0);
                        videoUrl = linkObj.getString(TAG_URL);

                        discs.add(buildDiscInfo(discId, videoUrl));
                    }

                    tracks.add(buildTrackInfo(trackId, audio, subtitle, discs));
                }

                sMovieListById.put(id, buildMovieInfo(id, name, description, imageUrl, released, tracks));
                movieList.add(buildMovieInfo(id, name, description, imageUrl, released, tracks));
            }

            sMovieList.put("", movieList);

            return sMovieList;
        }
    }

    private static MovieItem buildMovieInfo(int id, String name, String description, String imageUrl, String released, List<TrackItem> tracks) {
        MovieItem movie = new MovieItem();
        movie.setId(id);
        movie.setName(name);
        movie.setDescription(description);
        movie.setImageUrl(imageUrl);
        movie.setReleased(released);
        movie.setTracks(tracks);

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

    private static DiscItem buildDiscInfo(int id, String videoUrl) {
        DiscItem disc = new DiscItem();
        disc.setId(id);
        disc.setVideoUrl(videoUrl);

        return disc;
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