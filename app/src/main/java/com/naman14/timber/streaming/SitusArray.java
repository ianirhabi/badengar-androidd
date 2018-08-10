package com.naman14.timber.streaming;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SitusArray {

    @SerializedName("data")
    public ArrayList<SitusList> data;

    public ArrayList<SitusList> Getlistsitus() {
        return data;
    }
}
