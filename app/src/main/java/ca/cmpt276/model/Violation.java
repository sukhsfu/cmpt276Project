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
    private boolean hasManager;

    public Violation(int type, String severity, String detailedDescription, Boolean isRepeat) {
        manager = new ViolationManager();
        this.type = type;
        this.severity = severity;
        this.briefDescription = "";
        this.detailedDescription = detailedDescription;
        this.isRepeat = isRepeat;
        this.hasManager = false;
    }

    public ViolationManager getManager() {
        return manager;
    }

    public int getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }
}
