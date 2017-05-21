package com.iptv.iptv.main.event;

/**
 * Created by Asus N46V on 20/5/2017.
 */

public class PageLiveEvent {

    public final boolean hasNext;
    public final String nextUrl;

    public PageLiveEvent(boolean hasNext, String nextUrl) {
        this.hasNext = hasNext;
        this.nextUrl = nextUrl;
    }
}
