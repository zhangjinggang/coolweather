package com.example.inspire.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inspire on 2018/4/20.
 */

public class AQI {
    @SerializedName("city")
    public AQIcity aqicity;
    public class AQIcity{
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm25;
        @SerializedName("qlty")
        public String qlty;
    }
}
