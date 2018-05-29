package com.butel.mycoolweather.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.butel.mycoolweather.R;
import com.butel.mycoolweather.constant.HttpConstant;
import com.butel.mycoolweather.gson.ForeCast;
import com.butel.mycoolweather.gson.Weather;
import com.butel.mycoolweather.ui.fragment.ChooseAreaFragment;
import com.butel.mycoolweather.util.HttpUtil;
import com.butel.mycoolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by YangLin on 2018/4/5.
 * 遍历省市县级数据Fragment
 */

public class WeatherActivity extends AppCompatActivity {

    public static final String TAG = "WeatherActivity";
    public static final String WEATHER = "weather";
    public static final String BG_IMAGE = "bg_image";

    private Button navButton;//顶部的导航按钮图片(点击划出菜单选择省市县)
    public DrawerLayout drawer;//抽屉布局
    public SwipeRefreshLayout srl_weather;//下拉刷新控件
    private ImageView backgroundImg;//背景图片
    private ScrollView weatherLayout;
    private TextView titleCity;//城市名称
    private TextView titleUpdateTime;//更新时间
    private TextView temperatureNow;//现在的温度
    private TextView weatherInfoNow;//当前天气情况
    private LinearLayout forecastLayout;//未来一周天气预报
    private TextView aqiText;//AQI
    private TextView pm25Text;//PM2.5
    private TextView comfortText;//舒适度指数
    private TextView carWashText;//洗车指数
    private TextView sportText;//运动指数
    private String weatherId;//天气Id



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让背景图和状态栏融合在一起
        if (Build.VERSION.SDK_INT >= 21){
            //当系统的版本号大于或等于21
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        initView();
        //获取天气数据
        getWeatherData();
    }


    private void initView() {
        Log.i(TAG, "initView()");

        srl_weather = (SwipeRefreshLayout) findViewById(R.id.srl_weather);
        backgroundImg = (ImageView) findViewById(R.id.iv_weather_activity_bg);
        weatherLayout = (ScrollView) findViewById(R.id.sv_weather_layout);
        titleCity = (TextView) findViewById(R.id.tv_title_city);
        titleUpdateTime = (TextView) findViewById(R.id.tv_update_time);
        temperatureNow = (TextView) findViewById(R.id.tv_temperature);
        temperatureNow = (TextView) findViewById(R.id.tv_temperature);
        weatherInfoNow = (TextView) findViewById(R.id.tv_weather_info);
        forecastLayout = (LinearLayout) findViewById(R.id.ll_forecast);
        aqiText = (TextView) findViewById(R.id.tv_aqi);
        pm25Text = (TextView) findViewById(R.id.tv_pm25);
        comfortText = (TextView) findViewById(R.id.tv_comfort);
        carWashText = (TextView) findViewById(R.id.tv_car_wash);
        sportText = (TextView) findViewById(R.id.tv_sport);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.btn_nav);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开菜单
                drawer.openDrawer(Gravity.START);
            }
        });

    }

    private void getWeatherData() {
        //获取保存的首选项中的数据
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreference.getString(WEATHER, null);
        String imageUrl = sharedPreference.getString(BG_IMAGE, null);

        if (!TextUtils.isEmpty(weatherString)){
            Log.d(TAG, "getWeatherData(), 有缓存数据，直接解析天气数据");
            //有缓存的时候直接解析天气数据
            Weather weather = Utility.handlerWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            //处理天气数据
            showWeatherInfo(weather);
        }else {
            Log.d(TAG, "getWeatherData(), 没有缓存数据，想服务器请求天气数据");
            //无缓存时，weatherId
            weatherId = getIntent().getStringExtra(ChooseAreaFragment.WEATHER_ID);
            weatherLayout.setVisibility(View.INVISIBLE);
            //请求数据,再保存在缓存
            requestWeatherData(weatherId);
        }

        //设置下拉刷新控件的刷新监听
        srl_weather.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //请求新的天气数据
                requestWeatherData(weatherId);
            }
        });

        if (!TextUtils.isEmpty(imageUrl)){
            Glide.with(WeatherActivity.this).load(imageUrl).into(backgroundImg);
        }else {
            loadBgImage();
        }
    }


    /**
     * 根据天气id来请求城市天气信息
     * @param weatherId 天气id
     */
    public void requestWeatherData(String weatherId) {

        String weatherUrl = HttpConstant.HOST + "weather?cityid=" + weatherId + "&key=" + HttpConstant.KEY;

        Log.i(TAG, "requestWeatherData(), weatherId =" + weatherId + ", weatherUrl =" + weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseString = response.body().string();
                Log.d(TAG, "sendOkHttpRequest, onResponse, responseString =" + responseString);

                final Weather weather = Utility.handlerWeatherResponse(responseString);

                //切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            //把天气数据保存到首选项中
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(WEATHER, responseString);
                            editor.apply();
                            //处理天气数据并展示出来
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                        //停止刷新操作
                        srl_weather.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "sendOkHttpRequest, onFailure");
                e.printStackTrace();
                //切换到主线程，Toast提示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        //停止刷新操作
                        srl_weather.setRefreshing(false);
                    }

                });
            }

        });

        //每次请求天气信息的时候也去刷新下背景图片
        loadBgImage();
    }

    /**
     * 处理并展示Weather实体类中的具体数据
     * @param weather Weather类对象
     */
    private void showWeatherInfo(Weather weather) {
        Log.i(TAG, "showWeatherInfo()");

        //处理Weather中的数据
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String temperature = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        //给控件设置数据
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        temperatureNow.setText(temperature);
        weatherInfoNow.setText(weatherInfo);

        //移除未来天气布局里面的所有View
        forecastLayout.removeAllViews();
        for (ForeCast foreCast : weather.foreCastList) {
            //加载未来天气布局
            View view = LayoutInflater
                    .from(WeatherActivity.this)
                    .inflate(R.layout.forecast_item, forecastLayout, false);
            TextView date = (TextView) view.findViewById(R.id.tv_date);//日期
            TextView information = (TextView) view.findViewById(R.id.tv_information);//天气信息
            TextView upperTemperature = (TextView) view.findViewById(R.id.tv_upper_temperature);//最高温度
            TextView lowerTemperature = (TextView) view.findViewById(R.id.tv_lower_temperature);//最低温度

            //给控件设值
            date.setText(foreCast.date);
            information.setText(foreCast.more.info);
            upperTemperature.setText(foreCast.temperature.max);
            lowerTemperature.setText(foreCast.temperature.min);

            forecastLayout.addView(view);
        }

        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);

    }

    /**
     * 加载背景图片
     */
    private void loadBgImage() {
        //必应每日一图图片接口
        String requestImage = HttpConstant.HOST + "bing_pic";
        Log.i(TAG, "loadBgImage()");

        //进行okHttp请求，返回的是图片的url
        HttpUtil.sendOkHttpRequest(requestImage, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imageUrl = response.body().string();
                Log.d(TAG, "loadBgImage(), onResponse, url =" + imageUrl);

                //把服务器返回的背景图片地址保存到SharedPreferences中
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(BG_IMAGE, imageUrl);
                editor.apply();

                //切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //使用Glide加载图片
                        Glide.with(WeatherActivity.this).load(imageUrl).into(backgroundImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "loadBgImage(), onFailure");

                e.printStackTrace();

            }
        });
    }
}
