package com.iptv.iptv.main.event;

/**
 * Created by Asus N46V on 20/5/2017.
 */

public class PageSportEvent {

    public final boolean hasNext;
    public final String nextUrl;

    public PageSportEvent(boolean hasNext, String nextUrl) {
        this.hasNext = hasNext;
        this.nextUrl = nextUrl;
    }
}
