package com.iptv.iptv.main.model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by Karn on 22/4/2560.
 */

@Parcel
public class LiveProgramItem {

    private String programName;
    private String startTime;
    private String endTime;

    @ParcelConstructor
    public LiveProgramItem(String programName, String startTime, String endTime) {
        this.programName = programName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
