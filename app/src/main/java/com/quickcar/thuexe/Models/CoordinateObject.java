package com.quickcar.thuexe.Models;

/**
 * Created by DatNT on 10/7/2016.
 */

public class CoordinateObject {
    private double lon;
    private double lat;
    private String dateTime;
    private double distance;

    public CoordinateObject() {
    }

    public CoordinateObject(double lon, double lat, String dateTime, double distance) {
        this.lon = lon;
        this.lat = lat;
        this.dateTime = dateTime;
        this.distance = distance;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
