package com.dmtec.a91jiasu.models;

import android.content.Context;

import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomSharePreference;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dmtec on 2017/10/9.
 * Model of a jpush message
 */

public class JPushMessage implements Serializable{

    private String url;

    private String username;

    private String title;

    private String content;

    private String extras;

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private int type;


    public JPushMessage(String url, String username,String title, String content, String extras,String date,int type) {
        this.url = url;
        this.username = username;
        this.type = type;
        this.title = title;
        this.content = content;
        this.extras = extras;
        this.date = date;
    }

    public JPushMessage(Context constext, String url,String title, String content,String extras,String date) {
        this.url = url;
        this.type = 0;
        this.title = title;
        this.content = content;
        this.extras = extras;
        this.date = date;
        this.username = CustomSharePreference.getString(constext, Constants.Flags.SP_USERNAME);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("url", this.getUrl());
            jsonObject.put("type",this.getType());
            jsonObject.put("username",this.getUsername());
            jsonObject.put("title",this.getTitle());
            jsonObject.put("content",this.getContent());
            jsonObject.put("extras",this.getExtras());
            jsonObject.put("date",this.getDate());
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static JPushMessage parseMsg(String jsonString){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject = new JSONObject(jsonString);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JPushMessage(
            jsonObject.optString("url", ""),
            jsonObject.optString("username", ""),
            jsonObject.optString("title", ""),
            jsonObject.optString("content", ""),
            jsonObject.optString("extras", ""),
            jsonObject.optString("date", ""),
            jsonObject.optInt("type", 0)
        );
    }
}
