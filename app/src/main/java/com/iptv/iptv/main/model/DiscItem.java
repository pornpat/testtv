package com.iptv.iptv.main.model;

import org.parceler.Parcel;

/**
 * Created by Asus N46V on 3/3/2017.
 */

@Parcel
public class DiscItem {

    public DiscItem() {}

    private int id;
    private String videoUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
