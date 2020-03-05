package ca.cmpt276.model;

import java.util.List;

/**
 * Holds data of a single restaurant
 */

public class Restaurant {
    String name;
    String address;
    String city;
    double latitude;
    double longitude;
    List<Inspection> inspections;

    public Restaurant(String name, String address, String city, double latitude, double longitude,  List<Inspection> inspections) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.inspections = inspections;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() { return city; }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() { return longitude; }

    public List<Inspection> getInspections() {
        return inspections;
    }
}
