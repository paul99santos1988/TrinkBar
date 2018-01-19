package hs_ab.com.TrinkBar.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHours {

    @SerializedName("Thursday")
    @Expose
    private String thursday;
    @SerializedName("Friday")
    @Expose
    private String friday;
    @SerializedName("Saturday")
    @Expose
    private String saturday;
    @SerializedName("Sunday")
    @Expose
    private String sunday;
    @SerializedName("Monday")
    @Expose
    private String monday;
    @SerializedName("Tuesday")
    @Expose
    private String tuesday;
    @SerializedName("Wednesday")
    @Expose
    private String wednesday;

    /**
     * No args constructor for use in serialization
     *
     */
    public OpeningHours() {
    }

    /**
     *
     * @param wednesday
     * @param monday
     * @param thursday
     * @param sunday
     * @param saturday
     * @param tuesday
     * @param friday
     */
    public OpeningHours(String thursday, String friday, String saturday, String sunday, String monday, String tuesday, String wednesday) {
        super();
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
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

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
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