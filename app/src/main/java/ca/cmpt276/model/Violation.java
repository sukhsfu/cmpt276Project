package ca.cmpt276.model;

public class Violation {
    String nature;
    String severity;
    String briefDescription;
    String detailedDescription;

    public Violation(String nature, String severity, String briefDescription, String detailedDescription) {
        this.nature = nature;
        this.severity = severity;
        this.briefDescription = briefDescription;
        this.detailedDescription = detailedDescription;
    }

    public String getNature() {
        return nature;
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
