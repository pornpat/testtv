package com.iptv.iptv.main.model;

import org.parceler.Parcel;

/**
 * Created by Karn on 5/3/2560.
 */

@Parcel
public class SeriesEpisodeItem {

    private int order;
    private String url;

    public SeriesEpisodeItem() {}

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
