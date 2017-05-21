package com.iptv.iptv.main.event;

/**
 * Created by Asus N46V on 20/5/2017.
 */

public class PageSeriesEvent {

    public final boolean hasNext;
    public final String nextUrl;

    public PageSeriesEvent(boolean hasNext, String nextUrl) {
        this.hasNext = hasNext;
        this.nextUrl = nextUrl;
    }
}
