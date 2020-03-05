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
    private List<Violation> violations;

    public Inspection(String trackingNumber, String date, String type,
                       int numCriticalIssues, int numNonCriticalIssues, String hazardLevel) {
        this.trackingNumber = trackingNumber;
        this.date = date;
        this.type = type;
        this.numCriticalIssues = numCriticalIssues;
        this.numNonCriticalIssues = numNonCriticalIssues;
        this.hazardLevel = hazardLevel;
        this.violations = new ArrayList<Violation>();
    }

    public void addViolation(Violation violation) {
        violations.add(violation);
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
}
