package com.example.inspire.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inspire on 2018/4/20.
 */

public class Basic {
    @SerializedName("city")
    public String cityname;
    @SerializedName("id")
    public String weatherId;
    @SerializedName("update")
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }

}
