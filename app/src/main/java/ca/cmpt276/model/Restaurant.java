package ca.cmpt276.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds data of a single restaurant
 */

public class Restaurant {
    private String trackingNumber;
    private String name;
    private String address;
    private String city;
    private double latitude;
    private double longitude;
    private List<Inspection> inspections = new ArrayList<>();

    public Restaurant(String trackingNumber, String name, String address, String city, double latitude, double longitude) {
        this.trackingNumber = trackingNumber;
        this.name = name;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        //this.inspections = inspections;
    }

    public void addInspection (Inspection inspection) {
        inspections.add(inspection);
    }

    public String getTrackingNumber() {
        return trackingNumber;
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

    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", inspections=" + inspections.toString() +
                '}';
    }
}
