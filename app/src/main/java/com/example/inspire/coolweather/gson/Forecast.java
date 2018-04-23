package com.example.inspire.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inspire on 2018/4/20.
 */

public class Forecast {
    @SerializedName("date")
    public String date;
    @SerializedName("cond")
    public Condition condition;
    @SerializedName("tmp")
    public Temprature temprature;
    public class Condition{
        @SerializedName("txt_d")
        public String info;
    }
    public class Temprature{
        @SerializedName("max")
        public String max;
        @SerializedName("min")
        public String min;
    }
}
