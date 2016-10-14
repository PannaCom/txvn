package com.quickcar.thuexe.Models;

import java.io.Serializable;

/**
 * Created by DatNT on 6/28/2016.
 */
public class ActiveBusModel implements Serializable {
    private String name;
    private String fromPlace;
    private String toPlace;
    private String vehicleType;
    private double distance;
    private String telephone;
    private String datetime;
    private String start;
    private String end;

    public ActiveBusModel() {
    }

    public ActiveBusModel(String name, String fromPlace, String toPlace, String vehicleType, double distance, String telephone, String datetime, String start, String end) {
        this.name = name;
        this.fromPlace = fromPlace;
        this.toPlace = toPlace;
        this.vehicleType = vehicleType;
        this.distance = distance;
        this.telephone = telephone;
        this.datetime = datetime;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
