package hs_ab.com.TrinkBar.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bar {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("image_link")
    @Expose
    private String imageLink;
    @SerializedName("coordinates")
    @Expose
    private Coordinates coordinates;
    @SerializedName("image_data")
    @Expose
    private String imageData;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("food")
    @Expose
    private String food;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;

    /**
     * No args constructor for use in serialization
     *
     */
    public Bar() {
    }

    /**
     *
     * @param id
     * @param imageData
     * @param phone
     * @param openingHours
     * @param address
     * @param food
     * @param description
     * @param imageLink
     * @param name
     * @param coordinates
     */
    public Bar(String id, String name, String address, String phone, String imageLink, Coordinates coordinates, String imageData, String description, String food, OpeningHours openingHours) {
        super();
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.imageLink = imageLink;
        this.coordinates = coordinates;
        this.imageData = imageData;
        this.description = description;
        this.food = food;
        this.openingHours = openingHours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
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

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }
    
}