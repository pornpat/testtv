package com.iptv.iptv.main.test;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Asus N46V on 3/3/2017.
 */

@Parcel
public class TrackItem {

    private int id;
    private String audio;
    private String subtitle;
    private List<DiscItem> discs;

    public TrackItem() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<DiscItem> getDiscs() {
        return discs;
    }

    public void setDiscs(List<DiscItem> discs) {
        this.discs = discs;
    }
}
