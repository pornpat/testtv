package com.iptv.iptv.main.model;

import org.parceler.Parcel;

/**
 * Created by Asus N46V on 3/3/2017.
 */

@Parcel
public class DiscItem {

    private int id;
    private int discId;
    private String videoUrl;

    public DiscItem() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscId() {
        return discId;
    }

    public void setDiscId(int discId) {
        this.discId = discId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
