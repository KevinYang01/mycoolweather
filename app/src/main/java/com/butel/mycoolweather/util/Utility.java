package com.butel.mycoolweather.util;

import android.text.TextUtils;

import com.butel.mycoolweather.db.City;
import com.butel.mycoolweather.db.County;
import com.butel.mycoolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YangLin on 2018/4/5.
 * 处理和解析服务器返回的省市县数据，JSON格式
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     * @param response 返回的JSON字符串
     * @return 返回true: 解析成功  false：解析失败
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                //创建省级数据的JSON数组
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i ++){
                    //遍历数组，获取JSONObject对象
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    //创建Province类
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));//设值省的名字
                    province.setProvinceCode(provinceObject.getInt("id"));//设值省的代号
                    //将省级数据保存到数据库
                    province.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param response    返回的JSON字符串
     * @param provinceId  所属省的代号
     * @return   返回true: 解析成功  false：解析失败
     */
    public static boolean handleCityResponse(String response, int provinceId){
         if(!TextUtils.isEmpty(response)){
             try {
                 //创建市级数据的JSON数组
                 JSONArray allCities = new JSONArray(response);
                 for (int i = 0; i < allCities.length(); i ++){
                     //遍历数组，获取JSONObject对象
                     JSONObject cityObject = allCities.getJSONObject(i);
                     //创建City类
                     City city = new City();
                     city.setCityName(cityObject.getString("name"));//设置市的名字
                     city.setCityCode(cityObject.getInt("id"));//设置市的代号
                     city.setProvinceId(provinceId);//设置市所属省的代号
                     //将市级数据保存到数据库
                     city.save();
                 }

                 return true;
             } catch (JSONException e) {
                 e.printStackTrace();
             }
         }

         return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * @param response  返回的JSON字符串
     * @param cityId    所属市的代号
     * @return   返回true: 解析成功  false：解析失败
     */
    public static boolean handleCountyResponse(String response, int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                //创建县级数据的JSON数组
                JSONArray allCounties = new JSONArray(response);
                for(int i = 0; i < allCounties.length(); i ++){
                    //遍历数组，获取JSONObject对象
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    //创建County类
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));//设置县的名字
                    county.setWeatherId(countyObject.getString("weather_id"));//设置天气id
                    county.setCityId(cityId);//设置县所属市的代号
                    //将县级数据保存到数据库
                    county.save();
                }

                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
