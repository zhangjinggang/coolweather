package com.example.inspire.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.inspire.coolweather.gson.Forecast;
import com.example.inspire.coolweather.gson.Weather;
import com.example.inspire.coolweather.util.HttpUtil;
import com.example.inspire.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView qltyText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView imageView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;
    public Button btn_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorview=getWindow().getDecorView();
            decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.weather_activity);
        drawerLayout=(DrawerLayout) findViewById(R.id.draw_layout);
        btn_home=(Button)findViewById(R.id.home_button);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.SwipeRefresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        imageView=(ImageView) findViewById(R.id.image_back);
        weatherLayout=(ScrollView) findViewById(R.id.weather_scroll_layout);
        titleCity=(TextView) findViewById(R.id.title_city);
        titleUpdateTime=(TextView) findViewById(R.id.title_update_time);
        degreeText=(TextView) findViewById(R.id.degree);
        weatherInfoText=(TextView) findViewById(R.id.weather_info);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView) findViewById(R.id.aqi_text);
        pm25Text=(TextView) findViewById(R.id.pm25_text);
        comfortText=(TextView) findViewById(R.id.comfort_text);
        carWashText=(TextView) findViewById(R.id.car_wash_text);
        sportText=(TextView) findViewById(R.id.sport_text);
        qltyText=(TextView) findViewById(R.id.qlty_text);
        final String weather_id;
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr=preferences.getString("weather",null);
        if(weatherStr!=null){
            Weather weather= Utility.handleWeatherResponse(weatherStr);
            weather_id=weather.basic.weatherId;
            //显示天气数据
            showWeather(weather);
        }
        else{
            weatherLayout.setVisibility(ScrollView.INVISIBLE);
            //请求天气数据
            weather_id=getIntent().getStringExtra("weatherId");
            requestWeather(weather_id);
        }
        String loadimg=preferences.getString("bgimg_str",null);
        if(loadimg!=null){
            Glide.with(WeatherActivity.this).load(loadimg).into(imageView);
        }
        else{
            loadImg();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weather_id);
            }
        });
    }
    public void loadImg(){
        HttpUtil.sendOkhttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String strimg=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bgimg_str",strimg);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(strimg).into(imageView);
                    }
                });
            }
        });
    }
    public void requestWeather(String weatherId){
        HttpUtil.sendOkhttpRequest("http://guolin.tech/api/weather?cityid=" + weatherId + "&key=fcdde4d9d3e047968c614f042a901f10", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"the failer request one",Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weather_response=response.body().string();
                    final Weather weather=Utility.handleWeatherResponse(weather_response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weather!=null&&"ok".equals(weather.status)){
                               SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("weather",weather_response);
                                editor.apply();
                                //显示天气数据
                                showWeather(weather);
                            }
                            else{
                                Toast.makeText(WeatherActivity.this,"the failer request two",Toast.LENGTH_LONG).show();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

            }
        });
        loadImg();
    }
    public void showWeather(Weather weather){
        String cityName=weather.basic.cityname;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout,false);
            TextView dateText=(TextView) view.findViewById(R.id.date_text);
            TextView infoText=(TextView) view.findViewById(R.id.info_text);
            TextView maxText=(TextView) view.findViewById(R.id.max_text);
            TextView minText=(TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.condition.info);
            maxText .setText(forecast.temprature.max);
            minText .setText(forecast.temprature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi!=null){
            aqiText.setText(weather.aqi.aqicity.aqi);
            pm25Text.setText(weather.aqi.aqicity.pm25);
            qltyText.setText("空气质量:"+weather.aqi.aqicity.qlty);
        }
        String comfort="舒适度:"+weather.suggestion.comfort.info;
        String carWash="洗车指数:"+weather.suggestion.carWash.info;
        String sport="运动建议:"+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        //启动服务
        Intent intent=new Intent(this,AutoService.class);
        startService(intent);
    }



}
