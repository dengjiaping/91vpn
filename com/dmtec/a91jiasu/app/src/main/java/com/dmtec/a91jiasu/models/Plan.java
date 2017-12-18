package com.dmtec.a91jiasu.models;

import java.io.Serializable;

/**
 * Created by dmtec on 2017/8/20.
 *  套餐
 */

public class Plan implements Serializable {
    //id
    private String id;
    //套餐名
    private String name;
    //套餐描述
    private String desc;
    //套餐价格
    private String cost;
    //套餐时长
    private String duration;

    public Plan(String id, String name, String desc, String cost, String duration) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.cost = cost;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getCost() {
        return cost;
    }

    public String getDuration() {
        return duration;
    }
}
