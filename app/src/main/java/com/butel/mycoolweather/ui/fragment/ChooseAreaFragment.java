package com.butel.mycoolweather.ui.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.butel.mycoolweather.Constant.HttpConstant;
import com.butel.mycoolweather.R;
import com.butel.mycoolweather.db.City;
import com.butel.mycoolweather.db.County;
import com.butel.mycoolweather.db.Province;
import com.butel.mycoolweather.util.HttpUtil;
import com.butel.mycoolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by YangLin on 2018/4/5.
 * 遍历省市县级数据Fragment
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView title;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;

    //选中的省份
    private Province selectedProvince;
    //选中的市
    private City selectedCity;
    //当前的查询的级别
    private int currentLevel;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //获取标题和返回按钮控件
        title = (TextView) view.findViewById(R.id.tv_choose_area_title);
        backButton = (Button) view.findViewById(R.id.btn_choose_area_back);
        //获取ListView控件
        listView = (ListView) view.findViewById(R.id.lv_choose_area);
        //构建Adapter
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //刚开始查询中国所有的省份
        queryProvinces();

        //设置ListView的条目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    //选中某个省，就查询省下面的所以城市
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    //选中某个城市，就查询城市下面所有的县
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });

        //设置返回按钮的点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果当前查询结果是所有的县的页面，点击返回按钮就返回上面的市级页面
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    //如果当前查询结果是所有的市的页面，点击返回按钮就返回上面的省页面
                    queryProvinces();
                }
            }
        });
    }

    /**
     * 查询中国所有省份，优先从数据库总查询，如果没有查询到，就从服务器查询
     */
    private void queryProvinces() {
        title.setText("中国");
        backButton.setVisibility(View.GONE);
        //通过LitePal查询数据库,获取所有省的数据
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            //清空adapter中集合里面的所有数据
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            //刷新适配器
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = HttpConstant.HOST + "china";
            //从服务器查询
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询某个省份下面所有的城市，优先从数据库总查询，如果没有查询到，就从服务器查询
     */
    private void queryCities() {
        title.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        //通过LitePal查询数据库,获取省下面所有市的数据
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0){
            //清空适配器中集合里面的数据
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            //刷新适配器
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = HttpConstant.HOST + "china" + "/" + provinceCode;
            //从服务器查询
            queryFromServer(address, "city");
        }

    }

    /**
     * 查询某个城市下面所有的县，优先从数据库总查询，如果没有查询到，就从服务器查询
     */
    private void queryCounties() {
        title.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        //通过LitePal查询数据库,获取市下面所有县的数据
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            //清空适配器中集合里面的数据
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            //刷新适配器
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = HttpConstant.HOST + "china" + "/" + provinceCode + "/" + cityCode;
            //从服务器查询
            queryFromServer(address, "county");
        }
    }

    /**
     * 从服务器上查询省市县级数据
     * @param address 请求地址
     * @param type    类型
     */
    private void queryFromServer(String address, final String type) {
        //显示加载进度对话框
        showProgressDialog();
        //想服务器请求数据
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //服务器响应成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取服务器响应的数据
                String responseString = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseString);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseString, selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseString, selectedCity.getId());
                }

                if (result){
                    //切换到主线程
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //取消加载进度对话框
                            cancelProgressDialog();
                            if ("province".equals(type)){
                                //再次查询省级数据
                                queryProvinces();
                            }else if ("city".equals(type)){
                                //再次查询市级数据
                                queryCities();
                            }else if ("county".equals(type)){
                                //再次查询县级数据
                                queryCounties();
                            }
                        }
                    });
                }
            }

            //服务器响应失败
            @Override
            public void onFailure(Call call, IOException e) {
                //切换到主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //取消加载进度对话框
                        cancelProgressDialog();
                        Toast.makeText(getActivity(), "加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    //显示加载进度对话框
    private void showProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //取消加载对话框
    private void cancelProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
