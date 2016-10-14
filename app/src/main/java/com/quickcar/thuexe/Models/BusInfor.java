package com.quickcar.thuexe.Models;

import java.io.Serializable;

/**
 * Created by DatNT on 6/28/2016.
 */
public class BusInfor implements Serializable {
    private int id;
    private String carOwner;
    private String carPromote;
    private String fromPlace;
    private String toPlace;
    private String startTimeofDay;
    private String startTime;
    private String altstartTime;
    private String recepType;
    private String vehicleType;
    private int    price;
    private String telephone;
    private String note;

    public BusInfor(int id ,String carOwner, String carPromote, String fromPlace, String toPlace, String startTimeofDay, String startTime,String altstartTime, String recepType, String vehicleType, int price,String telephone, String note) {
        this.id                 = id;
        this.carOwner           = carOwner;
        this.carPromote         = carPromote;
        this.fromPlace          = fromPlace;
        this.toPlace            = toPlace;
        this.startTime          = startTime;
        this.recepType          = recepType;
        this.vehicleType        = vehicleType;
        this.price              = price;
        this.startTimeofDay     = startTimeofDay;
        this.telephone          = telephone;
        this.note               = note;
        this.altstartTime       = altstartTime;
    }

    public BusInfor() {
    }

    public String getAltstartTime() {
        return altstartTime;
    }

    public void setAltstartTime(String altstartTime) {
        this.altstartTime = altstartTime;
    }

    public String getStartTimeofDay() {
        return startTimeofDay;
    }

    public void setStartTimeofDay(String startTimeofDay) {
        this.startTimeofDay = startTimeofDay;
    }

    public String getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(String carOwner) {
        this.carOwner = carOwner;
    }

    public String getCarPromote() {
        return carPromote;
    }

    public void setCarPromote(String carPromote) {
        this.carPromote = carPromote;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getRecepType() {
        return recepType;
    }

    public void setRecepType(String recepType) {
        this.recepType = recepType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
