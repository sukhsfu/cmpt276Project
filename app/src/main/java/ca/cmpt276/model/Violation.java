package ca.cmpt276.model;
/**
 * Stores data of a single violation
 */
public class Violation {
    int type;
    String severity;
    String briefDescription;
    String detailedDescription;
    boolean isRepeat;

    public Violation(int type, String severity, String detailedDescription, Boolean isRepeat) {
        this.type = type;
        this.severity = severity;
        //this.briefDescription = briefDescription;
        this.detailedDescription = detailedDescription;
        this.isRepeat = isRepeat;
    }

    public String getSeverity() {
        return severity;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }
}
