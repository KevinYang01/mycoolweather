package com.butel.mycoolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.butel.mycoolweather.constant.HttpConstant;
import com.butel.mycoolweather.gson.Weather;
import com.butel.mycoolweather.ui.activity.WeatherActivity;
import com.butel.mycoolweather.util.HttpUtil;
import com.butel.mycoolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    public static final String TAG = "AutoUpdateService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //当开启服务的时候调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");

        updateWeather();
        updateImageBg();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int fourHour = 4 * 60 * 60 * 1000;//这是4个小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + fourHour;
        //设置4个小时到了后，又重新启动这个服务，这样保证了这个服务能够长期在后头运行，从而达到自动更新天气信息和背景图
        Intent intent1 = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent1, 0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        Log.i(TAG, "updateWeather()");

        //从首选项中获取之前保存的缓存数据
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString(WeatherActivity.WEATHER, null);
        if (!TextUtils.isEmpty(weatherString)){
            //有缓存时先解析出天气数据，从数据中拿到天气id
            Weather weather = Utility.handlerWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            //拿到这个天气id后再重新想服务器请求最新的天气数据
            String weatherUrl = HttpConstant.HOST + "weather?cityid=" + weatherId + "&key=" + HttpConstant.KEY;

            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherResponse = response.body().string();

                    //再解析刚请求到的天气数据
                    Weather weather = Utility.handlerWeatherResponse(weatherResponse);
                    if (weather != null && "ok".equals(weather.status)){
                        Log.i(TAG, "后头更新天气成功!");

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(WeatherActivity.WEATHER, weatherResponse);
                        editor.apply();

                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "后头更新天气失败! e:" + e);

                    e.printStackTrace();
                }
            });
        }
    }


    /**
     * 更新天气背景图
     */
    private void updateImageBg() {
        Log.i(TAG, "updateWeather()");

        String requestBgImageUrl = HttpConstant.HOST + "bing_pic";
        HttpUtil.sendOkHttpRequest(requestBgImageUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bgImageUrl = response.body().string();

                Log.i(TAG, "后台更新背景图成功, 图片url：" + bgImageUrl);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WeatherActivity.BG_IMAGE, bgImageUrl);
                editor.apply();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "后台更新背景图失败，e:" + e);
                e.printStackTrace();

            }
        });
    }
}
