package hs_ab.com.TrinkBar;

/**
 * Created by student on 08.06.17.
 */

public class PointOfInterest {
    Integer id;
    String name;
    String description;
    String image_link;

    PointOfInterest(int id, String name, String description, String image_link) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image_link = image_link;
    }
}
