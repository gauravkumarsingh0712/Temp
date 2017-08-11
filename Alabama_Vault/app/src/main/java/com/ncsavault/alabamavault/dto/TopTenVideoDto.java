package com.ncsavault.alabamavault.dto;

/**
 * Created by gauravkumar.singh on 5/17/2017.
 */

public class TopTenVideoDto {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoNumer() {
        return videoNumer;
    }

    public void setVideoNumer(String videoNumer) {
        this.videoNumer = videoNumer;
    }

    private String videoNumer;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
