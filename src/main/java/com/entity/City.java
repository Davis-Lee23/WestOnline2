package com.entity;

public class City {
    public String name;
    public int id;
    public double lat;
    public double lon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public City(int id) {
        this.id = id;
    }

    public City(){

    }

    public City(String name, int id, double lat, double lon) {
        this.name = name;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "城市id:"+id+",城市名称:"+name+",城市纬度:"+lat+",城市经度:"+lon+"\n";
    }
}
