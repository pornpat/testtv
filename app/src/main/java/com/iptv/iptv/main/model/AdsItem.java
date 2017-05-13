package com.iptv.iptv.main.model;

import org.parceler.Parcel;

/**
 * Created by Karn on 13/5/2560.
 */

@Parcel
public class AdsItem {

    private int id;
    private String title;
    private String description;
    private String imageUrl;

    public AdsItem() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
