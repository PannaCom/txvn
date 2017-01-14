package com.quickcar.thuexe.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hp on 1/13/2017.
 */

public class RentalInfo {
    private int id;
    private String carFrom;
    private String carTo;
    private String carType;
    private String carHireType;
    private int     carSize;
    private String phone;
    private String name;
    private String dateFrom;
    private String dateTo;
    private LatLng llFrom;
    private LatLng llTo;
    private int status;
    private double distance;

    public RentalInfo() {
    }

    public RentalInfo(int id, String carFrom, String carTo, String carType, String carHireType, int carSize, String phone, String name, String dateFrom, String dateTo, LatLng llFrom, LatLng llTo, int status, double distance) {
        this.id = id;
        this.carFrom = carFrom;
        this.carTo = carTo;
        this.carType = carType;
        this.carHireType = carHireType;
        this.carSize = carSize;
        this.phone = phone;
        this.name = name;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.llFrom = llFrom;
        this.llTo = llTo;
        this.status = status;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarFrom() {
        return carFrom;
    }

    public void setCarFrom(String carFrom) {
        this.carFrom = carFrom;
    }

    public String getCarTo() {
        return carTo;
    }

    public void setCarTo(String carTo) {
        this.carTo = carTo;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarHireType() {
        return carHireType;
    }

    public void setCarHireType(String carHireType) {
        this.carHireType = carHireType;
    }

    public int getCarSize() {
        return carSize;
    }

    public void setCarSize(int carSize) {
        this.carSize = carSize;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public LatLng getLlFrom() {
        return llFrom;
    }

    public void setLlFrom(LatLng llFrom) {
        this.llFrom = llFrom;
    }

    public LatLng getLlTo() {
        return llTo;
    }

    public void setLlTo(LatLng llTo) {
        this.llTo = llTo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
