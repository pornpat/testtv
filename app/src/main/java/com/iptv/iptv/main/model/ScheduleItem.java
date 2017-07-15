package com.iptv.iptv.main.model;

import java.util.List;

/**
 * Created by Asus N46V on 15/7/2017.
 */

public class ScheduleItem {

    private String type;
    private String name;
    private List<ScheduleSubItem> list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScheduleSubItem> getList() {
        return list;
    }

    public void setList(List<ScheduleSubItem> list) {
        this.list = list;
    }
}
