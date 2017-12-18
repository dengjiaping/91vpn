package com.dmtec.a91jiasu.common;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dmtec on 2017/7/31.
 * A common http client for http request
 */

public class CustomHttpClient {
    private static CustomHttpClient customHttpClient;
    private OkHttpClient client;

    private CustomHttpClient(){
        client = new OkHttpClient.Builder()
                .connectTimeout(Constants.Config.NETWORK_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.Config.NETWORK_CONNECT_TIMEOUT,TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();
    }

    //single instance
    public static CustomHttpClient getInstance(){
        if(customHttpClient == null){
            synchronized (CustomHttpClient.class){
                customHttpClient = new CustomHttpClient();
            }
        }
        return customHttpClient;
    }

    //async get
    public void get(String url, final HttpCallback callback){
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = "{}";
                try {
                    if(response.body() != null){
                        res = response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject result = null;
                try{
                    result = new JSONObject(res);
                }catch (Exception e){
                    e.printStackTrace();
                    result = new JSONObject();

                }
                callback.onSuccess(call,response,result);
            }
        });
    }

    //async post json text
    public void  post(String url, String postStr, final HttpCallback callback){
        Log.e("http-post-str",postStr);
        MediaType MEDIA_TYPE_JSON = MediaType.parse("text/json;charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_JSON,postStr))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = "{}";
                try {
                    if(response.body() != null){
                        res = response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject result = null;
                try{
                    result = new JSONObject(res);
                }catch (Exception e){
                    e.printStackTrace();
                    result = new JSONObject();

                }
                callback.onSuccess(call,response,result);
            }
        });
    }

    public void  postFormData(String url, HashMap<String,String> paramsMap, final HttpCallback callback){
        Log.e("Api-url:",url);
        FormBody.Builder builder = new FormBody.Builder();
        for(String key : paramsMap.keySet()){
            if(paramsMap.get(key)!=null){
                builder.add(key, paramsMap.get(key));
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = "{}";
                try {
                    if(response.body() != null){
                        res = response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject result;
                try{
                    result = new JSONObject(res);
                }catch (Exception e){
                    e.printStackTrace();
                    result = new JSONObject();
                }
                callback.onSuccess(call,response,result);
            }
        });
    }

    public void stream(String url, final HttpCallback callback){
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call,e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.body()!=null){
                    callback.onSuccess(call,response,new JSONObject());
                }
            }
        });
    }


    public interface HttpCallback{
        void onSuccess(Call call, Response response, JSONObject result);
        void onFailure(Call call, Exception e);
    }
}
