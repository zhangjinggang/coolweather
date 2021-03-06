package com.example.inspire.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.util.Util;
import com.example.inspire.coolweather.db.City;
import com.example.inspire.coolweather.db.County;
import com.example.inspire.coolweather.db.Province;
import com.example.inspire.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by inspire on 2018/4/19.
 */

public class Utility {
    private static final String TAG = "Utility";
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            JSONArray array= null;
            try {
                array = new JSONArray(response);
                for(int i=0;i<array.length();++i){
                    JSONObject jsonObject=array.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;

    }
    public static boolean handleCityResponse(String response,int ProvinceId){
        if(!TextUtils.isEmpty(response)){
            JSONArray array= null;
            try {
                array = new JSONArray(response);
                for(int i=0;i<array.length();++i){
                    JSONObject jsonObject=array.getJSONObject(i);
                    City city=new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(ProvinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;

    }
    public static boolean handleCountyResponse(String response,int CityId){
        if(!TextUtils.isEmpty(response)){
            JSONArray array= null;
            try {
                array = new JSONArray(response);
                for(int i=0;i<array.length();++i){
                    JSONObject jsonObject=array.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(jsonObject.getString("name"));
                    county.setCityId(CityId);
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;

    }
    public static Weather handleWeatherResponse(String response){
        try {

            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return null;
    }
}
