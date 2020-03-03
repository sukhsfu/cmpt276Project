package ca.cmpt276.data;

class InspectionSample {
    private String TrackingNumber;
    private int InspectionDate;
    private String InspType;
    private int NumCritical;
    private int NumNonCritical;
    private String HazardRating;
    private String ViolLump;


    public String getTrackingNumber() {
        return TrackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        TrackingNumber = trackingNumber;
    }

    public int getInspectionDate() {
        return InspectionDate;
    }

    public void setInspectionDate(int inspectionDate) {
        InspectionDate = inspectionDate;
    }

    public String getInspType() {
        return InspType;
    }

    public void setInspType(String inspType) {
        InspType = inspType;
    }

    public int getNumCritical() {
        return NumCritical;
    }

    public void setNumCritical(int numCritical) {
        NumCritical = numCritical;
    }

    public int getNumNonCritical() {
        return NumNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        NumNonCritical = numNonCritical;
    }

    public String getHazardRating() {
        return HazardRating;
    }

    public void setHazardRating(String hazardRating) {
        HazardRating = hazardRating;
    }

    public String getViolLump() {
        return ViolLump;
    }

    public void setViolLump(String violLump) {
        ViolLump = violLump;
    }


    @Override
    public String toString() {
        return "InspectionSample{" +
                "TrackingNumber='" + TrackingNumber + '\'' +
                ", InspectionDate='" + InspectionDate + '\'' +
                ", InspType='" + InspType + '\'' +
                ", NumCritical='" + NumCritical + '\'' +
                ", NumNonCritical='" + NumNonCritical + '\'' +
                ", HazardRating='" + HazardRating + '\'' +
                ", ViolLump='" + ViolLump + '\'' +
                '}';
    }
}
