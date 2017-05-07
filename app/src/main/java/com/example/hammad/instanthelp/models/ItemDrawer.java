package com.example.hammad.instanthelp.models;

/**
 * Created by Coder on 19/01/2017.
 */

public class ItemDrawer {

    private String name;
    private int imageId;

    public ItemDrawer(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}