package com.iptv.iptv.main.model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by Karn on 22/4/2560.
 */

@Parcel
public class LiveProgramItem {

    private String programName;
    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;

    @ParcelConstructor
    public LiveProgramItem(String programName, int startHour, int startMin, int endHour, int endMin) {
        this.programName = programName;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }
}
