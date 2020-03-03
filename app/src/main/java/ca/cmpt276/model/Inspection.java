package ca.cmpt276.model;

import java.util.List;

/**
 * Stores data of a single inspection
 */

public class Inspection {
    String date;
    String type;
    int numCriticalIssues;
    int numNonCriticalIssues;
    String hazardLevel;
    List<Violation> violations;

    public Inspection(String date, String type, int numCriticalIssues, int numNonCriticalIssues, String hazardLevel, List<Violation> violations) {
        this.date = date;
        this.type = type;
        this.numCriticalIssues = numCriticalIssues;
        this.numNonCriticalIssues = numNonCriticalIssues;
        this.hazardLevel = hazardLevel;
        this.violations = violations;
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
