package com.iptv.iptv.main.event;

/**
 * Created by Asus N46V on 20/5/2017.
 */

public class InformPageEvent {

    public final String prevUrl;
    public final String nextUrl;

    public InformPageEvent(String prevUrl, String nextUrl) {
        this.prevUrl = prevUrl;
        this.nextUrl = nextUrl;
    }
}
