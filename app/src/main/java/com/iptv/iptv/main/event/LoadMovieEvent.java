package com.iptv.iptv.main.event;

/**
 * Created by Karn on 25/3/2560.
 */

public class LoadMovieEvent {

    public final String url;

    public LoadMovieEvent(String url) {
        this.url = url;
    }

}
