package com.ncsavault.alabamavault.dto;

/**
 * Created by gauravkumar.singh on 7/19/2016.
 * This is dto of home screen data
 */
public class HomeDto {

    int id;
    String name;
    String address;
    String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
