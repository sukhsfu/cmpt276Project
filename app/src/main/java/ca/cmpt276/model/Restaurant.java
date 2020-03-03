package ca.cmpt276.model;

import java.util.List;

/**
 * Holds data of a single restaurant
 */

public class Restaurant {
    String name;
    String address;
    String gpsCoord;
    List<Inspection> inspections;

    public Restaurant(String name, String address, String gpsCoord, List<Inspection> inspections) {
        this.name = name;
        this.address = address;
        this.gpsCoord = gpsCoord;
        this.inspections = inspections;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getGpsCoord() {
        return gpsCoord;
    }

    public List<Inspection> getInspections() {
        return inspections;
    }
}
