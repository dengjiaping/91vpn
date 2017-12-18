package com.dmtec.a91jiasu.logic;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.common.CustomToast;
import com.dmtec.a91jiasu.models.JPushMessage;
import com.dmtec.a91jiasu.ui.MainActivity;
import com.dmtec.a91jiasu.ui.StrongSwanApplication;
import com.dmtec.a91jiasu.views.activities.LoginActivity;
import com.dmtec.a91jiasu.views.activities.SysMsgActivity;
import com.dmtec.a91jiasu.views.activities.WebActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.PushService;

/**
 * Created by dmtec on 2017/9/28.
 *
 */

public class JPushMsgReceiver extends BroadcastReceiver {

    private String initDate(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent pushintent=new Intent(context,PushService.class);//启动极光推送的服务
        context.startService(pushintent);

        Bundle bundle = intent.getExtras();
        String action = intent.getAction();
        Log.e("action:",action + "");
        //注册JPUSH
        if(action.equals("cn.jpush.android.intent.REGISTRATION")) {
            String regist_id = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.e("regist_id:",regist_id + "");
        }
        //自定义通知
        else if(action.equals("cn.jpush.android.intent.MESSAGE_RECEIVED")){
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e("jpush-title:",title + "");
            Log.e("jpush-message:",message + "");
            Log.e("jpush-extras:",extras + "");
            if(extras!=null){
                try {
                    JSONObject jsonObject = new JSONObject(extras);
                    String logout = jsonObject.optString("logout","0");
                    if(logout.equals("1")){
                        Intent i = new Intent(context, LoginActivity.class);
                        i.putExtras(bundle);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        Log.e("Contect",context.toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        //Notification通知
        else if(action.equals("cn.jpush.android.intent.NOTIFICATION_RECEIVED")){
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            String fileHtml = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
            String bigText = bundle.getString(JPushInterface.EXTRA_BIG_TEXT);
            String bigPicPath = bundle.getString(JPushInterface.EXTRA_BIG_PIC_PATH);
            String inboxJson = bundle.getString(JPushInterface.EXTRA_INBOX);
            String prio = bundle.getString(JPushInterface.EXTRA_NOTI_PRIORITY);
            try{
                JSONObject ex = new JSONObject(extras);
                String url = ex.optString("url","");
                CustomSharePreference.saveSysMsg(context,new JPushMessage(context,url,title,content,extras,initDate()));
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.e("JPush", content+"");
        }
        //点击通知后的动作
        else if(action.equals("cn.jpush.android.intent.NOTIFICATION_OPENED")){
            try {
                String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                String content = bundle.getString(JPushInterface.EXTRA_ALERT);
                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                String file = bundle.getString(JPushInterface.EXTRA_MSG_ID);
                Log.e("JPush", content+"");
                Intent i = new Intent(context, WebActivity.class);
                i.putExtras(bundle);
                String url="";
                try{
                    JSONObject ex = new JSONObject(extras);
                    url = ex.optString("url","");
                    i.putExtra("url",url);
                }catch (Exception e){
                    i.putExtra("url", "");
                    e.printStackTrace();
                }
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(!url.equals("")) {
                    context.startActivity(i);
                }else{
                    Intent smIntent = new Intent(context, SysMsgActivity.class);
                    smIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(smIntent);
                }
            }catch (Exception e){
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

}
