package com.iptv.iptv.main;

import com.iptv.iptv.R;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Karn on 8/4/2560.
 */

public class UrlUtil {

    public static final String BASE_URL = "http://139.59.231.135/uplay/public/api/v1";
    public static final String AUTH_URL = BASE_URL + "/auth";
    public static final String MOVIE_URL = BASE_URL + "/movies";
    public static final String SERIES_URL = BASE_URL + "/series";
    public static final String LIVE_URL = BASE_URL + "/lives";
    public static final String SPORT_URL = BASE_URL + "/sports";
    public static final String MOVIE_FILTER_URL = MOVIE_URL + "/filters";
    public static final String SERIES_FILTER_URL = SERIES_URL + "/filters";
    public static final String SPORT_FILTER_URL = SPORT_URL + "/filters";
    public static final String HISTORY_URL = BASE_URL + "/users/histories";
    public static final String FAVORITE_URL = BASE_URL + "/users/favorites";

    public static String addToken() {
        return "token=" + PrefUtil.getStringProperty(R.string.pref_token);
    }

    public static String appendUri(String uri, String appendQuery) {
        try {
            URI oldUri = new URI(uri);

            String newQuery = oldUri.getQuery();
            if (newQuery == null) {
                newQuery = appendQuery;
            } else {
                newQuery += "&" + appendQuery;
            }

            URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(),
                    oldUri.getPath(), newQuery, oldUri.getFragment());

            return newUri.toString();
        } catch (URISyntaxException e) {
            return uri;
        }
    }

    public static String addMediaId(String url, int id) {
        return url + "/" + id;
    }
}
