package ca.cmpt276.model;
/**
 * Stores data of a single violation
 */
public class Violation {
    private int type;
    private String severity;
    private String briefDescription;
    private String detailedDescription;
    private boolean isRepeat;
    private ViolationManager manager;

    public Violation(int type, String severity, String detailedDescription, Boolean isRepeat) {
        manager = new ViolationManager();
        this.type = type;
        this.severity = severity;
        this.briefDescription = manager.retrieve(type);
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
