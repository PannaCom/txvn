package com.quickcar.thuexe.Models;

/**
 * Created by DatNT on 10/7/2016.
 */

public class CarInforObject {
    private String name;
    private String phone;
    private String carModel;
    private String carMade;
    private String carType;
    private String carSize;
    private double distance;
    private int price;
    public CarInforObject() {
    }

    public CarInforObject(String name, String phone, String carModel, String carMade, String carType, String carSize, double distance, int price) {
        this.name = name;
        this.phone = phone;
        this.carModel = carModel;
        this.carMade = carMade;
        this.carType = carType;
        this.carSize = carSize;
        this.distance = distance;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarMade() {
        return carMade;
    }

    public void setCarMade(String carMade) {
        this.carMade = carMade;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarSize() {
        return carSize;
    }

    public void setCarSize(String carSize) {
        this.carSize = carSize;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
