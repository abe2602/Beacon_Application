package com.example.myapplication;

import java.io.Serializable;

public class TrackedThing implements Serializable {
    private String name;
    private String sensor;
    private String beaconMac;
    private boolean isAvailable;

    public TrackedThing(String name, String sensor, Boolean isAvailable){
        this.sensor = sensor;
        this.name = name;
        this.isAvailable = isAvailable;
        this.beaconMac = " ";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getBeaconMac() {
        return beaconMac;
    }

    public void setBeaconMac(String beaconMac) {
        this.beaconMac = beaconMac;
    }

}
