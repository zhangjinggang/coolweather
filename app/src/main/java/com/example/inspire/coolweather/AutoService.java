package com.example.inspire.coolweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.inspire.coolweather.gson.Weather;
import com.example.inspire.coolweather.util.HttpUtil;
import com.example.inspire.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoService extends Service {
    public AutoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
      //  throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updatePicture();
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        long time= SystemClock.elapsedRealtime()+8+60*60*1000;
        Intent i=new Intent(this,AutoService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateWeather(){
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr=pref.getString("weather",null);
        if(weatherStr!=null){
            Weather weather= Utility.handleWeatherResponse(weatherStr);
           String weather_id=weather.basic.weatherId;
            HttpUtil.sendOkhttpRequest("http://guolin.tech/api/weather?cityid=" + weather_id + "&key=fcdde4d9d3e047968c614f042a901f10", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weather_response=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(weather_response);
                    if(weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoService.this).edit();
                        editor.putString("weather",weather_response);
                        editor.apply();

                    }

                }
            });
        }
    }
    private void updatePicture(){
        HttpUtil.sendOkhttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String strimg=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoService.this).edit();
                editor.putString("bgimg_str",strimg);
                editor.apply();
            }
        });
    }
}
