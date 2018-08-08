package com.ridho.shareblood.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ridho on 3/22/2018.
 */

public class PlaceInfo {
    private String name;
    private String address;
    private String phoneNumber;
    private String id;
    private LatLng latLng;


    public PlaceInfo(String name,String address, String phoneNumber, String id, LatLng latLng) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.latLng = latLng;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String setAddress(String address) {
        this.address = address;
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public LatLng setLatLng(LatLng latLng) {
        this.latLng = latLng;
        return latLng;
    }

    public PlaceInfo(){

    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}
