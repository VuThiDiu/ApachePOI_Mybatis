package com.example.ApachePOIExample.model;


public class Ad {
    private long ad_id;
    private String ad_name;
    private String ad_status;
    private String ad_type;
    private float bid_modifier;

    public Ad() {
    }

    public Ad(long ad_id, String ad_name, String ad_status, String ad_type, float bid_modifier) {
        this.ad_id = ad_id;
        this.ad_name = ad_name;
        this.ad_status = ad_status;
        this.ad_type = ad_type;
        this.bid_modifier = bid_modifier;
    }

    public long getAd_id() {
        return ad_id;
    }

    public void setAd_id(long ad_id) {
        this.ad_id = ad_id;
    }

    public String getAd_name() {
        return ad_name;
    }

    public void setAd_name(String ad_name) {
        this.ad_name = ad_name;
    }

    public String getAd_status() {
        return ad_status;
    }

    public void setAd_status(String ad_status) {
        this.ad_status = ad_status;
    }

    public String getAd_type() {
        return ad_type;
    }

    public void setAd_type(String ad_type) {
        this.ad_type = ad_type;
    }

    public float getBid_modifier() {
        return bid_modifier;
    }

    public void setBid_modifier(float bid_modifier) {
        this.bid_modifier = bid_modifier;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "ad_id=" + ad_id +
                ", ad_name='" + ad_name + '\'' +
                ", ad_status='" + ad_status + '\'' +
                ", ad_type='" + ad_type + '\'' +
                ", bid_modifier=" + bid_modifier +
                '}';
    }
}
