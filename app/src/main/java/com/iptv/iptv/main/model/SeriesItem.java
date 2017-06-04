package com.iptv.iptv.main.model;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Karn on 5/3/2560.
 */

@Parcel
public class SeriesItem {

    private int id;
    private String name;
    private String engName;
    private String description;
    private String imageUrl;
    private String released;
    private List<SeriesTrackItem> tracks;

    public SeriesItem() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
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

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public List<SeriesTrackItem> getTracks() {
        return tracks;
    }

    public void setTracks(List<SeriesTrackItem> tracks) {
        this.tracks = tracks;
    }
}
