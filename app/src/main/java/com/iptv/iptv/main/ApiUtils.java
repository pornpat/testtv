package com.iptv.iptv.main;

import com.iptv.iptv.R;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Karn on 8/4/2560.
 */

public class ApiUtils {

    public static final String BASE_URL = "http://139.59.231.135/uplay/public/api/v1";
    public static final String AUTH_URL = BASE_URL + "/auth";
    public static final String AUTH_LOGOUT_URL = BASE_URL + "/auth/logout";
    public static final String MOVIE_URL = BASE_URL + "/movies";
    public static final String SERIES_URL = BASE_URL + "/series";
    public static final String LIVE_URL = BASE_URL + "/lives";
    public static final String SPORT_URL = BASE_URL + "/sports";
    public static final String VIP_URL = BASE_URL + "/vips";
    public static final String MOVIE_FILTER_URL = MOVIE_URL + "/filters";
    public static final String SERIES_FILTER_URL = SERIES_URL + "/filters";
    public static final String LIVE_FILTER_URL = LIVE_URL + "/filters";
    public static final String SPORT_FILTER_URL = SPORT_URL + "/filters";
    public static final String VIP_FILTER_URL = VIP_URL + "/filters";
    public static final String HISTORY_URL = BASE_URL + "/users/histories";
    public static final String MOVIE_HISTORY_URL = BASE_URL + "/users/histories/movies";
    public static final String SERIES_HISTORY_URL = BASE_URL + "/users/histories/series";
    public static final String LIVE_HISTORY_URL = BASE_URL + "/users/histories/lives";
    public static final String SPORT_HISTORY_URL = BASE_URL + "/users/histories/sports";
    public static final String HISTORY_DISC_URL = BASE_URL + "/users/histories/discs";
    public static final String HISTORY_EPISODE_URL = BASE_URL + "/users/histories/episodes";
    public static final String FAVORITE_URL = BASE_URL + "/users/favorites";
    public static final String MOVIE_FAVORITE_URL = BASE_URL + "/users/favorites/movies";
    public static final String SERIES_FAVORITE_URL = BASE_URL + "/users/favorites/series";
    public static final String LIVE_FAVORITE_URL = BASE_URL + "/users/favorites/lives";
    public static final String SPORT_FAVORITE_URL = BASE_URL + "/users/favorites/sports";
    public static final String RECOMMEND_URL = "/related";
    public static final String ADVERTISE_URL = BASE_URL + "/advertises/get";
    public static final String MOVIE_HIT_URL = MOVIE_URL + "?hit=VIEW&hit_days=7";
    public static final String SERIES_HIT_URL = SERIES_URL + "?hit=VIEW&hit_days=7";
    public static final String SPORT_HIT_URL = SPORT_URL + "?hit=VIEW&hit_days=7";
    public static final String VIP_HIT_URL = VIP_URL + "?hit=VIEW&hit_days=7";
    public static final String TOP_10_HIT_URL = MOVIE_URL + "/tops?days=7&limit=10";
    public static final String ALL_PACKAGE_URL = BASE_URL + "/packages/mypackages?noExpired=true";
    public static final String PACKAGE_URL = BASE_URL + "/packages";
    public static final String EXPIRE_CHECK_URL = BASE_URL + "/packages/expired";
    public static final String CHANGE_PASSWORD_URL = BASE_URL + "/auth/password";
    public static final String MEDIA_URL = BASE_URL + "/media";
    public static final String TIME_URL = BASE_URL + "/currentDateTime";
    public static final String NOTICE_URL = BASE_URL + "/notifies/last";

    public static final String SCHEDULE_URL = "http://service.iptvhero.com/moviecp/REST/v1/sport";
    public static final String TOPUP_BASE_URL = "http://cdnhispeed.com/mock_api/REST/V1/topup";
    public static final String TOPUP_PINCODE = TOPUP_BASE_URL + "/pincode";
    public static final String TOPUP_TRUE = TOPUP_BASE_URL + "/new";

    public static String addToken() {
        return "token=" + PrefUtils.getStringProperty(R.string.pref_token);
    }

    public static String addSession() {
        return "session=" + PrefUtils.getStringProperty(R.string.pref_token);
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

    public static String getRecommendUrl(String url, int id) {
        return url + "/" + id + RECOMMEND_URL;
    }

    public static String getRecentWatch(String url, int id) {
        return url + "/" + id;
    }

    public static String getTopupTransaction(int transactionId) {
        return TOPUP_BASE_URL + "/" + transactionId;
    }

    public static String getFavCheckUrl(int id) {
        return MEDIA_URL + "/" + id + "/isFavorite";
    }

    public static String getLiveUrlByKey(String key) {
        return LIVE_URL + "/contain?streamurl=" + key;
    }

}
