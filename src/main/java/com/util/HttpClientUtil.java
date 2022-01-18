package com.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpClientUtil {
    //我直接写我的和风key了
    static String key = "c590b579342b40a3a871109ecdf47a21";
    /**
     * ①可关闭的HttpClient客户端
     * ②此次程序只用了get请求，因此只设丽HttpGet对象
     * ③可关闭的返回对象
     */
    CloseableHttpClient closeableHttpClient =  HttpClients.createDefault();
    HttpGet httpGet = null;
    CloseableHttpResponse response = null;
    JacksonUtil jacksonUtil = new JacksonUtil();

    /**
     * 根据城市名字添加给数据库相应信息
     * @param city
     */
    public void addCity(String city) throws JsonProcessingException {
        jacksonUtil.addCity(getJson(city,0,0));
    }

    public void delete(int id){
        jacksonUtil.jdbcUtil.delete(id);
    }

    /**
     * 根据城市名称查询
     * @param city
     */
    public void findCity(String city) {
        jacksonUtil.jdbcUtil.queryCity(city);
    }

    /**
     * 查询数据库城市表中所有城市基本信息
     */
    public void findAllCity(){
        jacksonUtil.jdbcUtil.findAllCity();
    }

    /**
     * 根据id添加城市天气信息
     * @param id
     * @throws JsonProcessingException
     */
    public void addWeather(int id) throws JsonProcessingException {
        String json = this.getJson("",1,id);
        jacksonUtil.addWeather(json,id);
    }

    /**
     * 根据id查询城市天气信息
     * @param id
     */
    public void findCityWeather(int id){
        jacksonUtil.jdbcUtil.findCityWeather(id);
    }

    /**
     * 返回所有天气信息
     */
    public void findWeatherByDateAndId(){
        jacksonUtil.jdbcUtil.findWeatherByDateAndId();
    }

    /**
     * function代表url对城市信息发起访问还是对天气信息发起访问
     * 若为0，则使用city变量，获得城市信息字符串，将其返回给调用者
     * 若为1，则使用id变量，同理
     * @param city
     * @param function
     * @param id
     * @return
     */
    public String getJson(String city,int function,int id){
        String url="";
        if(function == 0) {
            url = "https://geoapi.qweather.com/v2/city/lookup?key="+key+"&location="+city;
        }
        if(function == 1) {
            url = "https://devapi.qweather.com/v7/weather/3d?location="+id+"&key="+key;
        }
        //赋值HttpGet请求对象，对其发送get请求
        httpGet = new HttpGet(url);
        String json="";
        try {
            //给响应对象赋值
            response = closeableHttpClient.execute(httpGet);
            //获得本次返回的状态码，若为200（SC_OK）返回成功
            if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
                //获取响应结果
                HttpEntity entity = response.getEntity();
                //获得了浏览器返回的json格式字符串
                json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    //关闭流
    public void close(){
        if (closeableHttpClient != null) {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(response != null){
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
