package ca.cmpt276.model;

import java.util.List;

/**
 * Holds data of a single restaurant
 */

public class Restaurant {
    private String name;
    private String address;
    private String city;
    private double latitude;
    private double longitude;
    private List<Inspection> inspections;

    public Restaurant(String name, String address, String city, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        //this.inspections = inspections;
    }

    public void addInspections (List<Inspection> inspections) {
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
