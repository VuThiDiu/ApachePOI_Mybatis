package com.example.ApachePOIExample.model;


import java.util.Date;

public class Campaign {
    private long campaign_id;
    private String campaign_name;
    private String campaign_status;
    private Date start_date;
    private Date end_date;
    private float budget;

    public Campaign() {
    }

    public Campaign(int campaign_id, String campaign_name, String campaign_status, Date start_date, Date end_date, float budget) {
        this.campaign_id = campaign_id;
        this.campaign_name = campaign_name;
        this.campaign_status = campaign_status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.budget = budget;
    }

    public long getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(long campaign_id) {
        this.campaign_id = campaign_id;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }

    public String getCampaign_status() {
        return campaign_status;
    }

    public void setCampaign_status(String campaign_status) {
        this.campaign_status = campaign_status;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "campaign_id=" + campaign_id +
                ", campaign_name='" + campaign_name + '\'' +
                ", campaign_status='" + campaign_status + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", budget=" + budget +
                '}';
    }
}
