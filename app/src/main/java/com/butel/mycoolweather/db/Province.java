package com.butel.mycoolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by YangLin on 2018/4/3.
 * 省级实体类
 */

public class Province extends DataSupport{

    private int id;               //唯一标识id
    private String provinceName;  //省的名字
    private int provinceCode;     //省的代号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
