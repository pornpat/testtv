package com.iptv.iptv.main.event;

/**
 * Created by Asus N46V on 20/5/2017.
 */

public class PageMovieEvent {

    public final boolean hasNext;
    public final String nextUrl;

    public PageMovieEvent(boolean hasNext, String nextUrl) {
        this.hasNext = hasNext;
        this.nextUrl = nextUrl;
    }
}
