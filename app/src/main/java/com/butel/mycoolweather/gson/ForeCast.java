package com.butel.mycoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YangLin on 2018/4/8.
 * 未来天气信息实体类
 */

public class ForeCast {

    @SerializedName("date")
    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        @SerializedName("max")
        public String max;

        @SerializedName("min")
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
