package com.iptv.iptv.main.model;

/**
 * Created by Asus N46V on 15/7/2017.
 */

public class ScheduleSubItem {

    private String league;
    private String time;
    private String date;
    private int duration;
    private String teamName1;
    private String teamLogo1;
    private String teamName2;
    private String teamLogo2;
    private String channelName;
    private String channelStream;

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTeamName1() {
        return teamName1;
    }

    public void setTeamName1(String teamName1) {
        this.teamName1 = teamName1;
    }

    public String getTeamLogo1() {
        return teamLogo1;
    }

    public void setTeamLogo1(String teamLogo1) {
        this.teamLogo1 = teamLogo1;
    }

    public String getTeamName2() {
        return teamName2;
    }

    public void setTeamName2(String teamName2) {
        this.teamName2 = teamName2;
    }

    public String getTeamLogo2() {
        return teamLogo2;
    }

    public void setTeamLogo2(String teamLogo2) {
        this.teamLogo2 = teamLogo2;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelStream() {
        return channelStream;
    }

    public void setChannelStream(String channelStream) {
        this.channelStream = channelStream;
    }
}
