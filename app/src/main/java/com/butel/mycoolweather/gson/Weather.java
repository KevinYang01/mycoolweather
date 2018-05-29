package com.butel.mycoolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by YangLin on 2018/4/8.
 * 天气总的实例类
 */

public class Weather {

    //成功返回ok，失败返回具体的原因
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<ForeCast> foreCastList;

}
