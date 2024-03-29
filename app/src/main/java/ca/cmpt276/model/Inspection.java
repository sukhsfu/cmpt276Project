package ca.cmpt276.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores data of a single inspection
 */

public class Inspection {
    private String trackingNumber;
    private String date;
    private String type;
    private int numCriticalIssues;
    private int numNonCriticalIssues;
    private String hazardLevel;
//    private String voli;
    private List<Violation> violations;

    public Inspection(String trackingNumber, String date, String type,
                       int numCriticalIssues, int numNonCriticalIssues, String hazardLevel) {
        this.trackingNumber = trackingNumber;
        this.date = date;
        this.type = type;
        this.numCriticalIssues = numCriticalIssues;
        this.numNonCriticalIssues = numNonCriticalIssues;
        this.hazardLevel = hazardLevel;
//        this.voli = voli;
        this.violations = new ArrayList<>();
    }

    public void addViolation(Violation violation) {
        violations.add(violation);
    }
//    public String getVoli(){return voli;}

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public int getNumCriticalIssues() {
        return numCriticalIssues;
    }

    public int getNumNonCriticalIssues() {
        return numNonCriticalIssues;
    }

    public String getHazardLevel() {

        return hazardLevel;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    @Override
    public String toString() {
        return "Inspection{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", numCriticalIssues=" + numCriticalIssues +
                ", numNonCriticalIssues=" + numNonCriticalIssues +
                ", hazardLevel='" + hazardLevel + '\'' +
//                ", voli = '" + voli + '\'' +
                ", violations=" + violations +
                '}';
    }


}
