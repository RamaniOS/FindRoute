package com.example.findroute;

public class Place {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Place(String name, String vicinity, String latitude, String longitude, String reference) {
        this.name = name;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reference = reference;
    }

    public Place() {

    }

    String name, vicinity, latitude, longitude, reference;
}
