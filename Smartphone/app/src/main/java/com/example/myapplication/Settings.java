package com.example.myapplication;

public class Settings {
    private boolean hasNotification;
    private int range;

    public Settings(int range, boolean hasNotification){
        this.range = range;
        this.hasNotification = hasNotification;
    }

    public Boolean getHasNotification() {
        return hasNotification;
    }

    public void setHasNotification(Boolean hasNotification) {
        this.hasNotification = hasNotification;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

}
