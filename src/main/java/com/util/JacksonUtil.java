package com.util;

import com.entity.City;
import com.entity.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 基于Jackson实现对返回json转对象的处理
 */
public class JacksonUtil {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode node = null;
    JdbcUtil jdbcUtil = new JdbcUtil();

    //向数据库添加城市数据
    public void addCity(String json) throws JsonProcessingException {
        //获得返回的json数组
        node = getJsonNodeArray(json,0);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        //判断一下是不是数组
        if(node.isArray()){
            for(JsonNode o : node){
                //转换为city类对象处理
                City city = objectMapper.readValue(o.toString(),City.class);
                if(jdbcUtil.cityIsExist(city)) {
                    System.out.println("数据库中已存在"+city.getName()+"基本信息");
                    continue;
                }
                jdbcUtil.add(city);
            }
        }
    }

    //这个同理可得
    public void addWeather(String json,int id) throws JsonProcessingException {
        node = getJsonNodeArray(json,1);
        if(node.isArray()){
            for(JsonNode o : node){
                Weather weather = objectMapper.readValue(o.toString(),Weather.class);
                weather.setId(id);
                City city = new City(weather.getId());
                if(!jdbcUtil.cityIsExist(city)){
                    System.out.println("城市表中不存在该城市.请先添加相应城市");
                    continue;
                }
                if(jdbcUtil.weatherIsExist(weather)){
                    System.out.println("城市表中存在该城市,将对该城市天气数据刷新");
                    jdbcUtil.updateWeather(weather,id);
                }else {
                    jdbcUtil.addWeather(weather,id);
                    System.out.println("城市天气添加成功");
                }
            }
        }
    }

    /**
     * 根据func来返回不同功能
     * 若为0，代表接收的json是城市基本信息json
     * 若为1，代表接收的是城市天气json
     * @param json
     * @param func
     * @return
     */
    public JsonNode getJsonNodeArray(String json,int func){
        //将无关于对象的json属性无视掉，只接受相应的数据
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        try {
            //根据对应字符串X:[]，在X之后的{}json数组会一个个切割掉
            //如：location[{a:1},{b:2}]切成两个返回
            if(func == 0)node = new ObjectMapper().readTree(json).get("location");
            if(func == 1)node = new ObjectMapper().readTree(json).get("daily");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return node;
    }
}
