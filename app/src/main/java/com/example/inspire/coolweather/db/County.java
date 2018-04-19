package com.example.inspire.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by inspire on 2018/4/19.
 */

public class County extends DataSupport {
    private int Id;
    private String CountyName;
    private String weatherId;
    private int CityId;
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(String countyName) {
        CountyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return CityId;
    }

    public void setCityId(int cityId) {
        CityId = cityId;
    }


}
