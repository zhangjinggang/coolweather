package com.example.inspire.coolweather;

import android.app.ProgressDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inspire.coolweather.db.City;
import com.example.inspire.coolweather.db.County;
import com.example.inspire.coolweather.db.Province;
import com.example.inspire.coolweather.util.HttpUtil;
import com.example.inspire.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist=new ArrayList<>();  //适配器的list
    private List<Province> provinceList;             //查询省返回的list
    private List<City> cityList;                       //查询市返回的list
    private List<County> countyList;                    //查询县返回的list
    private Province selectedProvince;
    private City selectedCity;
    private int CurrentdLevel;


    public ChooseAreaFragment() {
        // Required empty public constructor
    }
//先写几个功能函数
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
   //从服务器查询
    private void QueryFromServer(String address, final String type){
         showProgressDialog();
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"failure",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean result=false;
                //将从服务器获取省市县数据写入本地数据库
                    switch (type){
                        case "province":{
                            result=Utility.handleProvinceResponse(response.body().string());
                        }
                        break;
                        case "city":{
                            result=Utility.handleCityResponse(response.body().string(),selectedProvince.getId());
                        }
                        break;
                        case "county":{
                            result=Utility.handleCountyResponse(response.body().string(),selectedCity.getId());
                        }
                        break;
                        default:
                            break;
                    }
                    //从本地数据库中读取省市县数据并显示
                    if(result){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                switch (type){
                                    case "province":{
                                       //显示省
                                        queryProvince();
                                    }
                                    break;
                                    case "city":{
                                        //显示市
                                        queryCity();
                                    }
                                    break;
                                    case "county":{
                                        //显示县
                                        queryCounty();
                                    }
                                    break;
                                    default:
                                        break;
                                }
                            }
                        });
                    }
            }
        });
    }
    //查询省份
    private void queryProvince(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        datalist.clear();
        provinceList=DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            for(Province province:provinceList){
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            CurrentdLevel=LEVEL_PROVINCE;
        }
        else{
            QueryFromServer("http://guolin.tech/api/china","province");
        }

    }
    //查询市
    private void queryCity(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        datalist.clear();
        cityList=DataSupport.where("ProvinceId=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            for(City city:cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            CurrentdLevel=LEVEL_CITY;
        }
        else{
            QueryFromServer("http://guolin.tech/api/china/"+selectedProvince.getProvinceCode(),"city");
        }

    }
    //查询县
    private void queryCounty(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        datalist.clear();
        countyList=DataSupport.where("CityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            for(County county:countyList){
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
           // listView.setSelection(0);
            CurrentdLevel=LEVEL_COUNTY;
        }
        else{
            QueryFromServer("http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode(),"county");
        }

    }

    public static ChooseAreaFragment newInstance(String param1, String param2) {
        ChooseAreaFragment fragment = new ChooseAreaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.choose_area,container);
        backButton=view.findViewById(R.id.button_back);
        listView=view.findViewById(R.id.list_view);
        titleText=view.findViewById(R.id.title_text);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvince();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(CurrentdLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(i);
                    queryCity();
                }
                else if(CurrentdLevel==LEVEL_CITY){
                    selectedCity=cityList.get(i);
                    queryCounty();
                }
                else if(CurrentdLevel==LEVEL_COUNTY){
                    String weatherId=countyList.get(i).getWeatherId();
                    if(getActivity()instanceof MainActivity){
                        Intent intent=new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weatherId",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if(getActivity()instanceof WeatherActivity){
                        WeatherActivity weatherActivity=(WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.swipeRefreshLayout.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);
                    }

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentdLevel==LEVEL_COUNTY){
                    queryCity();
                }
                else if(CurrentdLevel==LEVEL_CITY){
                    queryProvince();
                }
            }
        });
    }
}
