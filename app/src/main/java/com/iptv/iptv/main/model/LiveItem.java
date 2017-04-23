package com.iptv.iptv.main.model;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Karn on 5/3/2560.
 */

@Parcel
public class LiveItem {

    private int id;
    private String name;
    private String logoUrl;
    private String url;
    private List<LiveProgramItem> programs;

    public LiveItem() {}

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

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<LiveProgramItem> getPrograms() {
        return programs;
    }

    public void setPrograms(List<LiveProgramItem> programs) {
        this.programs = programs;
    }
}
