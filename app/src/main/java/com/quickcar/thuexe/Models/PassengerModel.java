package com.quickcar.thuexe.Models;

/**
 * Created by hp on 8/11/2016.
 */
public class PassengerModel {
    private String uiid;
    private String name;
    private String phone;
    private String note;
    private boolean isConfirm;
    private int driverId;
    public PassengerModel() {
    }

    public PassengerModel(String uiid, String name, String phone, String note, int driverId, boolean isConfirm) {
        this.uiid = uiid;
        this.name = name;
        this.phone = phone;
        this.note = note;
        this.driverId = driverId;
        this.isConfirm = isConfirm;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getUiid() {
        return uiid;
    }

    public void setUiid(String uiid) {
        this.uiid = uiid;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }
}
