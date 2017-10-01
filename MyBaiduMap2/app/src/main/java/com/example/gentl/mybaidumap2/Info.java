package com.example.gentl.mybaidumap2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gentl on 2017/7/18.
 */
public class Info implements Serializable{

    private double latitude;
    private double longtitude;
    private int imgId;
    private String name;

    public static List<Info> infos=new ArrayList<Info>();
    static
    {
        infos.add(new Info(34.242652,108.971171,R.drawable.a01,"英伦贵族小旅馆","距离209米",1456));
        infos.add(new Info(34.242952,108.972171,R.drawable.a02,"沙井国际洗浴会所","距离897米",456));
        infos.add(new Info(34.242852,108.973171,R.drawable.a03,"五环服装城","距离249米",12456));
        infos.add(new Info(34.242152,108.971971,R.drawable.a04,"老米家泡馍小炒","距离679米",2456));
    }
    public Info(double latitude, double longtitude, int imgId, String name, String distance, int zan) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.zan = zan;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }

    private String distance;
    private int zan;
}
