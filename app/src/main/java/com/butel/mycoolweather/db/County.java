package com.butel.mycoolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by YangLin on 2018/4/3.
 * 县级实体类
 */

public class County extends DataSupport{

    private int id;              //唯一标识id
    private String countyName;   //县的名字
    private String weatherId;    //县所对应的天气id
    private int cityId;          //该县所属城市的id值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
