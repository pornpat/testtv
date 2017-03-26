package com.iptv.iptv.main.event;

/**
 * Created by Asus N46V on 26/3/2017.
 */

public class ApplyFilterEvent {

    public final boolean isApplied;

    public ApplyFilterEvent(boolean isApplied) {
        this.isApplied = isApplied;
    }

}
