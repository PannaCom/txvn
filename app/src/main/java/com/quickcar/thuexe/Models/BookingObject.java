package com.quickcar.thuexe.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DatNT on 11/29/2016.
 */

public class BookingObject {
    private int     id;
    private String  carFrom;
    private String  carTo;
    private String  carType;
    private String  carHireType;
    private String  carSize;
    private String  phone;
    private String  name;
    private String  dateFrom;
    private String  dateTo;
    private String  dateTime;
    private LatLng  lFrom;
    private LatLng  lTo;
    private String  status;
    private double  distance;

    public BookingObject() {
    }

    public BookingObject(int id, String carFrom, String carTo, String carType, String carHireType, String carSize, String phone, String name, String dateFrom, String dateTo, String dateTime, LatLng lFrom, LatLng lTo, String status, double distance) {
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
        this.dateTime = dateTime;
        this.lFrom = lFrom;
        this.lTo = lTo;
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

    public String getCarSize() {
        return carSize;
    }

    public void setCarSize(String carSize) {
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public LatLng getlFrom() {
        return lFrom;
    }

    public void setlFrom(LatLng lFrom) {
        this.lFrom = lFrom;
    }

    public LatLng getlTo() {
        return lTo;
    }

    public void setlTo(LatLng lTo) {
        this.lTo = lTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
