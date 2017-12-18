package com.dmtec.a91jiasu.logic;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.PingUtil;
import com.dmtec.a91jiasu.models.IPServer;
import com.dmtec.a91jiasu.ui.StrongSwanApplication;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by dmtec on 2017/8/21.
 *
 */

public class VpnWorker {

    private Context context;
    private GifImageView loader;
    private Activity activity;
    private  ArrayList<IPServer> ikev20Array = new ArrayList<>();
    private  ArrayList<IPServer> ikev21Array = new ArrayList<>();
    private  ArrayList<IPServer> ss0Array = new ArrayList<>();
    private  ArrayList<IPServer> ss1Array = new ArrayList<>();
    public IPServer bestIKEV20Server = null;
    public IPServer bestIKEV21Server = null;
    public IPServer bestSS0Server = null;
    public IPServer bestSS1Server = null;

    public VpnWorker(Context context, Activity activity,GifImageView loader) {
        this.context = context;
        this.loader = loader;
        this.activity = activity;
    }


    public void start(){
        //状态设置
        loader.setClickable(false);
        loader.setImageResource(R.drawable.running);
    }


    /**
     * 预先找出ping值最低的ip
     */
    public void prepare(){
        ikev20Array = new ArrayList<>();
        ikev21Array = new ArrayList<>();
        ss0Array = new ArrayList<>();
        ss1Array = new ArrayList<>();
        bestIKEV20Server = null;
        bestIKEV21Server = null;
        bestSS0Server = null;
        bestSS1Server = null;
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        if(ikev20Array.size() > 0){
                            PingUtil.chooseIP(ikev20Array, new PingUtil.IPChooseCallback() {
                                @Override
                                public void onResult(final IPServer ipServer) {
                                    bestIKEV20Server = ipServer;
                                    Log.e("IKEv20 server:",ipServer.getIP() + " " +  ipServer.getRtt() + " ms");
                                }
                            });
                        }
                        if(ikev21Array.size() > 0) {
                            PingUtil.chooseIP(ikev21Array, new PingUtil.IPChooseCallback() {
                                @Override
                                public void onResult(final IPServer ipServer) {
                                    bestIKEV21Server = ipServer;
                                    Log.e("IKEv21 server:", ipServer.getIP() + " " + ipServer.getRtt() + " ms");
                                }
                            });
                        }
                        if(ss0Array.size() > 0) {
                            PingUtil.chooseIP(ss0Array, new PingUtil.IPChooseCallback() {
                                @Override
                                public void onResult(final IPServer ipServer) {
                                    bestSS0Server = ipServer;
                                    Log.e("SS0 server:", ipServer.getIP() + " " + ipServer.getRtt() + " ms");
                                }
                            });
                        }
                        if(ss1Array.size() > 0) {
                            PingUtil.chooseIP(ss1Array, new PingUtil.IPChooseCallback() {
                                @Override
                                public void onResult(final IPServer ipServer) {
                                    bestSS1Server = ipServer;
                                    Log.e("SS1 server:", ipServer.getIP() + " " + ipServer.getRtt() + " ms");
                                }
                            });
                        }
                        break;
                }
            }
        };
        new LogicHelper(context).getServer(((StrongSwanApplication) activity.getApplication()).getToken(), new Callbacks.GetServerCallback() {
            @Override
            public void onServerList(ArrayList<IPServer> ikev20,ArrayList<IPServer> ikev21,ArrayList<IPServer> ss0,ArrayList<IPServer> ss1) {
                fillArray(ikev20,ikev20Array);
                fillArray(ikev21,ikev21Array);
                fillArray(ss0,ss0Array);
                fillArray(ss1,ss1Array);
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    private void fillArray(ArrayList<IPServer> list,ArrayList<IPServer> toFill){
        for(int i = 1; i <= list.size(); i++){
            IPServer server = list.get(i-1);
            server.setIP(PingUtil.GetInetAddress(server.getHost()));
            toFill.add(server);
        }
    }
}
