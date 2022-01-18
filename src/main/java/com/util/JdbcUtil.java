package com.util;

import com.entity.City;
import com.entity.Weather;

import java.sql.*;

public class JdbcUtil {
    /**
     * 你们改改URL哈，这个是挺久前写的工具类，凑合着用了
     * 没用mybatis，直接用jdbc了
     */
    final String URL = "jdbc:mysql://localhost:3306/west";
    final String USERNAME = "root";
    final String PASSWORD = "123456";
    PreparedStatement ps = null;
    Connection con = null;
    ResultSet rs =null;

    //将jar包中的driver实现类加载到JVM中
    static {
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //封装链接通道细节
    public Connection getCon(){
        try{
            con = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return con;
    }

    //获取预编译的MySQL对象
    public PreparedStatement createStatement(String sql){
        try{
            //预编译一条sql语句
            ps = getCon().prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ps;
    }

    //添加城市基本数据
    public void add(City city){
        String sql = "insert into city(id,name,lat,lon)"+
                " value(?,?,?,?)";
        ps = createStatement(sql);
        try {
            ps.setInt(1, city.getId());
            ps.setString(2,city.getName());
            ps.setDouble(3,city.getLat());
            ps.setDouble(4,city.getLon());
            ps.executeUpdate();
            System.out.println("成功添加"+city);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * 根据id，一次性将城市和天气表的数据都删除
     * @param id
     */
    public void delete(int id){
        String sql = "delete from city where id=?";
        ps = createStatement(sql);
        try {
            ps.setInt(1,id);
            ps.executeUpdate();
            sql = "delete from weather where id=?";
            ps = createStatement(sql);
            ps.setInt(1,id);
            ps.executeUpdate();
            System.out.println("成功删除id为"+id+"的城市");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            close();
        }
    }

    /**
     * 根据城市名称查询数据
     * @param name
     */
    public void queryCity(String name){
        String sql = "select * from city where name=?";
        PreparedStatement ps = createStatement(sql);
        try {
            ps.setString(1,name);
            rs = ps.executeQuery();
            while(rs.next()) {
                System.out.println("城市ID:"+rs.getInt("id")
                        +" 城市名称:"+rs.getString("name")
                        +" 城市纬度:"+rs.getString("lat")
                        +" 城市经度"+rs.getString("lon")
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            close();
        }
    }

    //查询所有城市的基本数据
    public void findAllCity(){
        String sql = "select * from city";
        PreparedStatement ps = createStatement(sql);
        try {
            rs = ps.executeQuery();
            while(rs.next()) {
                System.out.println("城市ID:"+rs.getInt("id")
                        +" 城市名称:"+rs.getString("name")
                        +" 城市纬度:"+rs.getString("lat")
                        +" 城市经度"+rs.getString("lon")
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            close();
        }
    }

    //检查是否已有城市基本数据
    public boolean cityIsExist(City city){
        boolean flag = false;
        String sql = "select * from city where id=?";
        PreparedStatement ps = createStatement(sql);
        try {
            ps.setInt(1,city.getId());
            rs = ps.executeQuery();
            while(rs.next()) {
                if (rs.getInt("id") == city.getId()) flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            close();
        }
        return flag;
    }

    /**
     * 返回的字符串没有转换成城市名称，因为“两个晚上，一个奇迹”赶工（🐕）
     * @param id
     */
    public void findCityWeather(int id){
        String sql = "select * from weather where id=? order by fxDate desc";
        ps = createStatement(sql);
        boolean flag = false;
        try {
            ps.setInt(1,id);
            rs =  ps.executeQuery();
            while(rs.next()){
                flag = true;
                System.out.println("城市ID:"+rs.getInt("id")
                        +",日期:"+rs.getString("fxDate")
                        +",今日最高温:"+rs.getString("tempMax")
                        +"℃,今日最低温:"+rs.getString("tempMin")
                        +"℃,今日天气:"+rs.getString("textDay")
                );
            }
            if(!flag) {
                System.out.println("没有相应城市数据");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 返回所有天气信息
     */
    public void findWeatherByDateAndId(){
        String sql = "select * from weather order by fxDate desc,id asc";
        ps = createStatement(sql);
        try {
            rs = ps.executeQuery();
            while(rs.next()){
                System.out.println("城市ID:"+rs.getInt("id")
                        +",日期:"+rs.getString("fxDate")
                        +",今日最高温:"+rs.getString("tempMax")
                        +"℃,今日最低温:"+rs.getString("tempMin")
                        +"℃,今日天气:"+rs.getString("textDay")
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addWeather(Weather weather,int id){
        String sql = "insert into weather(id,fxDate,tempMax,tempMin,textDay) " +
                "values (?,?,?,?,?)";
        ps = createStatement(sql);
        try {
            ps.setInt(1,id);
            ps.setString(2,weather.getFxDate());
            ps.setInt(3,weather.getTempMax());
            ps.setInt(4,weather.getTempMin());
            ps.setString(5,weather.getTextDay());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            close();
        }
    }

    public void updateWeather(Weather weather,int id){
        if (!weatherIsExist(weather)){
            System.out.println("该城市或该城市本日期信息不存在,请先添加");
            return;
        }
        String sql = "update weather set tempMax=?,tempMin=?,textDay=? where (id=? and fxDate=?)";
        ps = createStatement(sql);
        try {
            ps.setInt(1,weather.getTempMax());
            ps.setInt(2,weather.getTempMin());
            ps.setString(3,weather.getTextDay());
            ps.setInt(4,id);
            ps.setString(5,weather.getTextDay());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            close();
        }
    }

    /**
     * 检测数据库中是否存在对应三日天气数据
     * 在此之前先检查城市是否存在，不存在没意义
     * @param weather
     * @return
     */
    public boolean weatherIsExist(Weather weather){
        boolean flag = false;
        String sql = "select * from weather where id=? and fxDate=?";
        ps = createStatement(sql);
        try {
            ps.setInt(1,weather.getId());
            ps.setString(2,weather.getFxDate());
            rs = ps.executeQuery();
            while (rs.next()){
                if(rs.getInt("id")==weather.getId() && rs.getString("fxDate").equals(weather.getFxDate()))flag=true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }

    //关闭流
    public void close(){
        if(ps != null){
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(con != null){
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void close(ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(ps != null){
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(con != null){
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
