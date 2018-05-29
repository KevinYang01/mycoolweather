package com.butel.mycoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YangLin on 2018/4/8.
 * 天气基本信息实体类
 *
 * 由于JSON中的一些字段不太适合直接作为Java字段来命名，因为这里使用了@SerializedName注解的方式
 * 来让JSON字段和Java字段之间建立映射关系
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("update")
    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
