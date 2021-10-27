package com.tanvirhossen.dollarbucks.model;

public class YoutubeModel {
    private String url, time, cpc;

    public YoutubeModel(String url, String time, String cpc) {
        this.url = url;
        this.time = time;
        this.cpc = cpc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCpc() {
        return cpc;
    }

    public void setCpc(String cpc) {
        this.cpc = cpc;
    }
}
