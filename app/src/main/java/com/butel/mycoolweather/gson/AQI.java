package com.butel.mycoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YangLin on 2018/4/8.
 * AQI实体类
 */

public class AQI {

    @SerializedName("city")
    public AQICity city;

    public class AQICity{
        @SerializedName("aqi")
        public String aqi;

        @SerializedName("pm25")
        public String pm25;
    }
}
