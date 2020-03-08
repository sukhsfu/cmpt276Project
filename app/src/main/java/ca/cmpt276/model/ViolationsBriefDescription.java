package ca.cmpt276.model;

/**
 * Store brief description of a violation
 */

public class ViolationsBriefDescription {
    private int type;
    private String description;

    public ViolationsBriefDescription(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }
}
