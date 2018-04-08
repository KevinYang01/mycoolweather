package com.butel.mycoolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by YangLin on 2018/4/3.
 * Http网络请求工具类
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
