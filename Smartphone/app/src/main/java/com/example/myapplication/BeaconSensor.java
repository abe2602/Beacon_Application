package com.example.myapplication;

public class BeaconSensor {
    private String mac;
    private String name;
    private Boolean isAvailable;

    public BeaconSensor(String mac, String name, Boolean isAvailable){
        this.mac = mac;
        this.name = name;
        this.isAvailable = isAvailable;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
}
