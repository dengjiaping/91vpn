package com.dmtec.a91jiasu.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.dmtec.a91jiasu.models.JPushMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dmtec on 2017/8/20.
 * 持久化存储基本数据类型的数据
 */

public class CustomSharePreference {
    private static final String APP_TAG = "91VPN";
    private static final String MESSAGE_TAG = "91VPN_JPUSH_MSG";


    public static void putString(Context context,String key, String value){
        SharedPreferences sp = context.getSharedPreferences(APP_TAG,Context.MODE_PRIVATE);
        sp.edit().putString(key,value).apply();
    }

    public static void putInteger(Context context,String key, int value){
        SharedPreferences sp = context.getSharedPreferences(APP_TAG,Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).apply();
    }

    public static String getString(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(APP_TAG,Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }

    public static int getInt(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(APP_TAG,Context.MODE_PRIVATE);
        return sp.getInt(key,-1);
    }

    public static void saveSysMsg(Context context, JPushMessage message){
        ArrayList<JPushMessage> msgs = getJPushMessages(context);
        msgs.add(message);
        JSONArray jsonArray = new JSONArray();
        try{
            for(JPushMessage m : msgs){
                JSONObject object = new JSONObject(m.toString());
                jsonArray.put(object);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        SharedPreferences sp = context.getSharedPreferences(MESSAGE_TAG, Context.MODE_PRIVATE);
        sp.edit().putString("MESSAGE",jsonArray.toString()).apply();
    }

    public static ArrayList<JPushMessage> getJPushMessages(Context context){
        ArrayList<JPushMessage> messages = new ArrayList<>();
        JSONArray jsonArray;
        SharedPreferences sp = context.getSharedPreferences(MESSAGE_TAG,Context.MODE_PRIVATE);
        String arrayStr = sp.getString("MESSAGE","");
        try{
            jsonArray = new JSONArray(arrayStr);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                JPushMessage msg = JPushMessage.parseMsg(object.toString());
                messages.add(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return messages;
    }


}
