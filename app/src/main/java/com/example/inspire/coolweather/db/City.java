package com.example.inspire.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by inspire on 2018/4/19.
 */

public class City extends DataSupport {
    private int Id;
    private String CityName;
    private int CityCode;
    private int ProvinceId;
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public int getCityCode() {
        return CityCode;
    }

    public void setCityCode(int cityCode) {
        CityCode = cityCode;
    }

    public int getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(int provinceId) {
        ProvinceId = provinceId;
    }


}
