package hs_ab.com.TrinkBar.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bars {

    @SerializedName("bars")
    @Expose
    private List<Bar> bars = null;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;

    public List<Bar> getBars() {
        return bars;
    }

    public void setBars(List<Bar> bars) {
        this.bars = bars;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}