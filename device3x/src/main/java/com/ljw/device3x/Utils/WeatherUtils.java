package com.ljw.device3x.Utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.showweather.model.db.entities.minimalist.WeatherLive;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.ljw.device3x.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class WeatherUtils {
    //private static String weatherUrl = "http://cdn.weather.hao.360.cn/api_weather_info.php?app=hao360&_jsonp=data&code=";//根据城市获取天气信息URL
    private static String weatherUrl = "http://service.envicloud.cn:8082/v2/weatherlive/ZMF3ZWLNDWKXNDKZMZU5ODQ4NZE2/";//根据城市获取天气信息URL

    private ImageView weatherImg;
    private TextView tmpValues;
    private Map<String, String> mapAllNameID;

    public WeatherUtils(ImageView iv, TextView tv) {
        this.weatherImg = iv;
        this.tmpValues = tv;
        initCityCodeTable();
    }

    /**
     * 根据当前的城市代码获取相应的天气信息
     */
    private void getWeatherInfoByCityCode(String url) {
        HttpUtils mUtils = new HttpUtils();
        Log.i("ljwtestweather", "城市代码的url:" + url);
        mUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.i("ljwtestweather","原始数据:" + responseInfo.result);
   //             String weatherinfo = cutJsonString(Utils.unicodeToString(Utils.formatJsonString(responseInfo.result)));
//                log_i(weatherinfo);
                parseWeatherInfo(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("ljwtestweather", "onFailure" + s);
            }
        });
    }

    /**
     * 初始化城市代码
     */
    private void initCityCodeTable() {
        mapAllNameID = new HashMap<String, String>();
        NameIdMap nameIDMap = new NameIdMap();
        mapAllNameID = nameIDMap.getMapAllNameID();
    }

    /**
     * 初始化城市代码
     */
    public String getCityCode(String cityName) {
        if (TextUtils.isEmpty(cityName))
            return mapAllNameID.get("深圳");
       return mapAllNameID.get(cityName);
    }
    /**
     * 剪切字符串，这个url返回的JSON数据有个包头没用
     */
    private String cutJsonString(String info) {
        return info.substring(5, info.length() - 2);
    }

    /**
     * 解析更新天气信息
     */
    private void parseWeatherInfo(String info) {
        getWeatherInfo(parseWhichDayForcastInfo(info, 0));
    }

    /**
     * 取得第几天天气预报信息
     */
    private JSONObject parseWhichDayForcastInfo(String info, int which) {
        try {
            JSONObject jo = new JSONObject(info);
          //  JSONArray ja = jo.getJSONArray("weather");
          //  return (JSONObject) ja.get(which);
            return  jo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解析并存入GridView的Item的数据
     */
    private void getWeatherInfo(JSONObject jsonObject) {
        if (jsonObject == null)
            return;
        try {

            Log.i("ljwtestweather", "天气是:" + jsonObject.get("phenomena").toString());
            Log.i("ljwtestweather", "温度是:" + jsonObject.get("temperature").toString());
            int weatherNumber = getWeatherImg(jsonObject.get("phenomena").toString());
            String weather = jsonObject.get("phenomena").toString();
            String tmp = jsonObject.get("temperature").toString();


            if(weatherNumber != 0)
                weatherImg.setImageResource(weatherNumber);
            if(!TextUtils.isEmpty(tmp)) {
                if (tmp.contains("."))
                    tmp = tmp.substring(0,tmp.indexOf("."));
                tmpValues.setText(weather + " " + tmp + "℃");
            }
        } catch (JSONException e) {
            Log.i("ljwtestweather","解析错误:" + e.toString());
            e.printStackTrace();
        }
    }

    public void updateWeatherInfo(String originCityName) {
        String url = "";
        if(TextUtils.isEmpty(originCityName))
            url = weatherUrl + mapAllNameID.get("深圳");
        else
            url = weatherUrl + mapAllNameID.get(originCityName);
        Log.i("ljwtestweather", "拼凑的天气URL是" + url);
        getWeatherInfoByCityCode(url);
    }

    /**
     * 根据天气信息设置天气图片（小图）
     *
     * @param cond 天气信息
     * @return 对应的天气图片id
     */
    private int getWeathersmallImg(String cond) {
        int img = 0;
        int length = cond.length();
        if (cond.contains("晴") && length <= 2)
            img = R.mipmap.forecast_sun;
        else if (cond.contains("多云"))
            img = R.mipmap.forecast_cloud;
        else if (cond.contains("阴") && length <= 2)
            img = R.mipmap.forecast_overcast;
        else if (cond.contains("雷"))
            img = R.mipmap.forecast_thunderandrain;
        else if (cond.contains("雨")) {
            if (cond.contains("小雨"))
                img = R.mipmap.forecast_smallrain;
            else if (cond.contains("中雨"))
                img = R.mipmap.forecast_middlerain;
            else if (cond.contains("大雨"))
                img = R.mipmap.forecast_bigrain;
            else if (cond.contains("雨夹雪"))
                img = R.mipmap.forecast_rainandsnow;
            else if (cond.contains("暴雨"))
                img = R.mipmap.forecast_stormrain;
            else
                img = R.mipmap.forecast_smallrain;
        } else if (cond.contains("雪")) {
            if (cond.contains("小雪"))
                img = R.mipmap.forecast_smallsnow;
            else if (cond.contains("中雪"))
                img = R.mipmap.forecast_middlesnow;
            else
                img = R.mipmap.forecast_smallsnow;
        } else
            img = R.mipmap.forecast_sun;
        return img;
    }

    /**
     * 根据天气信息设置天气图片（大图）
     *
     * @param cond 天气信息
     * @return 对应的天气图片id
     */
    private int getWeatherImg(String cond) {
        int img = 0;
        int length = cond.length();
        if (cond.contains("晴"))
            img = R.mipmap.weather_sun;
        else if (cond.contains("多云"))
            img = R.mipmap.weather_cloud;
        else if (cond.contains("阴"))
            img = R.mipmap.weather_overcast;
        else if (cond.contains("雷"))
            img = R.mipmap.weather_thunderandrain;
        else if (cond.contains("雨")) {
            if (cond.contains("小雨"))
                img = R.mipmap.weather_smallrain;
            else if (cond.contains("中雨"))
                img = R.mipmap.weather_middlerain;
            else if (cond.contains("大雨"))
                img = R.mipmap.weather_bigrain;
            else if (cond.contains("雨夹雪"))
                img = R.mipmap.weather_rainandsnow;
            else if (cond.contains("暴雨"))
                img = R.mipmap.weather_stormrain;
            else
                img = R.mipmap.weather_smallrain;
        } else if (cond.contains("雪")) {
            if (cond.contains("小雪"))
                img = R.mipmap.weather_smallsnow;
            else if (cond.contains("中雪"))
                img = R.mipmap.weather_middlesnow;
            else
                img = R.mipmap.weather_smallsnow;
        } else
            img = R.mipmap.weather_sun;
        return img;
    }

    public void updateWeatherInfo(WeatherLive weatherlive) {
        Log.i("ljwtestweather", "天气是:" + weatherlive.getWeather());
        Log.i("ljwtestweather", "温度是:" + weatherlive.getTemp());
        int weatherNumber = getWeatherImg(weatherlive.getWeather());
        String weather = weatherlive.getWeather();
        String tmp = weatherlive.getTemp();


        if(weatherNumber != 0)
            weatherImg.setImageResource(weatherNumber);
        if(!TextUtils.isEmpty(tmp))
            if (tmp.contains("."))
                tmp = tmp.substring(0,tmp.indexOf("."));
            tmpValues.setText(weather+" "+tmp+"℃");
    }
}
