package com.quickcar.thuexe.Models;

import java.io.Serializable;

/**
 * Created by DatNT on 8/9/2016.
 */
public class OwnerCarObject implements Serializable {
    private int id;
    private String ownerName;
    private String startPlace;
    private String endPlace;
    private String type;
    private String provinceFrom;
    private String provinceTo;
    public OwnerCarObject() {
    }

    public OwnerCarObject(int id, String ownerName, String startPlace, String endPlace, String type, String provinceFrom, String provinceTo) {
        this.id = id;
        this.ownerName = ownerName;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.type = type;
        this.provinceFrom = provinceFrom;
        this.provinceTo = provinceTo;
    }

    public String getProvinceFrom() {
        return provinceFrom;
    }

    public void setProvinceFrom(String provinceFrom) {
        this.provinceFrom = provinceFrom;
    }

    public String getProvinceTo() {
        return provinceTo;
    }

    public void setProvinceTo(String provinceTo) {
        this.provinceTo = provinceTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }
    public int returnIdFromName(String name, String start ,String end){
        if (getOwnerName().equals(name) && getStartPlace().equals(start) && getEndPlace().equals(end))
            return getId();
        return 0;
    }
}
