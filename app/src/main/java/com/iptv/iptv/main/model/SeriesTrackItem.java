package com.iptv.iptv.main.model;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Karn on 5/3/2560.
 */

@Parcel
public class SeriesTrackItem {

    private int id;
    private String subtitle;
    private String audio;
    private List<SeriesEpisodeItem> episodes;

    public SeriesTrackItem() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public List<SeriesEpisodeItem> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<SeriesEpisodeItem> episodes) {
        this.episodes = episodes;
    }
}
