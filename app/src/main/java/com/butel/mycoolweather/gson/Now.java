package com.butel.mycoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YangLin on 2018/4/8.
 * Now实体类
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
