package hs_ab.com.TrinkBar.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import hs_ab.com.TrinkBar.models.Bar;

public class Bars {

    @SerializedName("bars")
    @Expose
    private List<Bar> bars = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Bars() {
    }

    /**
     *
     * @param bars
     */
    public Bars(List<Bar> bars) {
        super();
        this.bars = bars;
    }

    public List<Bar> getBars() {
        return bars;
    }

    public void setBars(List<Bar> bars) {
        this.bars = bars;
    }

}