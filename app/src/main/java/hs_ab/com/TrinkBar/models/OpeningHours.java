package hs_ab.com.TrinkBar.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHours {

    @SerializedName("friday")
    @Expose
    private String friday;
    @SerializedName("monday")
    @Expose
    private String monday;
    @SerializedName("saturday")
    @Expose
    private String saturday;
    @SerializedName("sunday")
    @Expose
    private String sunday;
    @SerializedName("thursday")
    @Expose
    private String thursday;
    @SerializedName("tuesday")
    @Expose
    private String tuesday;
    @SerializedName("wednesday")
    @Expose
    private String wednesday;

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

}