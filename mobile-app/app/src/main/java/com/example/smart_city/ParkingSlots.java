package com.example.smart_city;

import com.google.gson.annotations.SerializedName;

public class ParkingSlots {
    private int id;
    private String name;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lon;
    @SerializedName("status")
    private int status;

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public String getName() {
        return name;
    }

    public double getLon() {
        return lon;
    }

    public int getStatus() {
        return status;
    }
}
