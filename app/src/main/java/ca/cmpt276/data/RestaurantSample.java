package ca.cmpt276.data;

class RestaurantSample {
    private String TrackingNumber;
    private String Name;
    private String Address;
    private String Physicalcity;
    private String FaceType;
    private Double Lat;
    private Double Lon;

    public String getTrcackingNumber() {
        return TrackingNumber;
    }

    public void setTrackingNumber(String trcackingNumber) {
        TrackingNumber = trcackingNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhysicalcity() {
        return Physicalcity;
    }

    public void setPhysicalcity(String physicalcity) {
        Physicalcity = physicalcity;
    }

    public String getFaceType() {
        return FaceType;
    }

    public void setFaceType(String faceType) {
        FaceType = faceType;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLon() {
        return Lon;
    }

    public void setLon(Double lon) {
        Lon = lon;
    }

    @Override
    public String toString() {
        return "RestaurantSample{" +
                "TrackingNumber='" + TrackingNumber + '\'' +
                ", Name='" + Name + '\'' +
                ", Address='" + Address + '\'' +
                ", Physicalcity='" + Physicalcity + '\'' +
                ", FaceType='" + FaceType + '\'' +
                ", Lat=" + Lat +
                ", Lon=" + Lon +
                '}';
    }
}