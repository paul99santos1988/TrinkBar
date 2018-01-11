package hs_ab.com.TrinkBar;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bars {

    @SerializedName("bars")
    @Expose
    private List<Bar> bars = null;

    public List<Bar> getBars() {
        return bars;
    }

    public void setBars(List<Bar> bars) {
        this.bars = bars;
    }

}