package hs_ab.com.TrinkBar.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bar {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("coordinates")
    @Expose
    private Coordinates coordinates;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("food")
    @Expose
    private String food;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("visitor")
    @Expose
    private String visitor;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("openingHours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("phone")
    @Expose
    private String phone;

    private String imageData;

    private String distance;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVisitor() {return visitor;}

    public void setVisitor(String visitor) {this.visitor = visitor;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageData() {return imageData;}

    public void setImageData(String imageData) {this.imageData = imageData;}

    public void setDistance(String distance){this.distance = distance;}

    public String getDistance(){return distance;}

}