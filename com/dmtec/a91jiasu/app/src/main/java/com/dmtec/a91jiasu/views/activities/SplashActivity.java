package com.dmtec.a91jiasu.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.common.Validator;
import com.dmtec.a91jiasu.logic.BaseActivity;
import com.dmtec.a91jiasu.logic.Callbacks;
import com.dmtec.a91jiasu.logic.CertificateProvider;
import com.dmtec.a91jiasu.logic.LogicHelper;
import com.dmtec.a91jiasu.ui.MainActivity;
import com.dmtec.a91jiasu.ui.StrongSwanApplication;
import com.dmtec.a91jiasu.utils.Utils;

import java.io.InputStream;

public class SplashActivity extends BaseActivity {

    private TextView progress;
    private String content;
    public static int SERVER_AREA = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progress = (TextView)findViewById(R.id.splash_progress);
        content = "";
        new LogicHelper(getApplicationContext()).isAbroad(new Callbacks.AreaCallback() {
            @Override
            public void onResult(boolean isAbroad) {
                Constants.useAbroadService = isAbroad;
                Constants.isAroad = isAbroad;
                checkCertificate();
            }
        });
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    content="\n查询证书安装状态";
                    break;
                case 1:
                    content="\n查询证书版本号";
                    break;
                case 2:
                    content="\n开始下载证书";
                    break;
                case 3:
                    content="\n证书已是最新版";
                    break;
                case 4:
                    content="\n证书下载完成，正在检测证书";
                    break;
                case 5:
                    content="\n正在配置证书信息";
                    break;
                case 6:
                    content="\n配置完成";
                    break;
                case 7:
                    content="\n正在进入应用";
                    break;
            }
            progress.setText(content + " 版本号：V1.7");
        }
    };

    /**
     * 检测证书可用性
     */
    private void checkCertificate(){
        handler.sendEmptyMessage(0);
        //查询证书是否需要安装、更新
        LogicHelper logicHelper = new LogicHelper(this);
        int localVersion = CustomSharePreference.getInt(getApplicationContext(),Constants.Config.CERTIFICATE_VERSION);
        Log.e("localversion",localVersion+"");
        logicHelper.queryCert(localVersion, new Callbacks.QueryCertCallback() {
            @Override
            public void onResult(boolean success, boolean isLatest,int version, String curl) {
                handler.sendEmptyMessage(1);
                //需要重新安装证书
                if(success && (!isLatest)){
                    handler.sendEmptyMessage(2);
                    downloadCert(curl,version);
                }else{
                    Log.e("isLatest",isLatest+"");
                    handler.sendEmptyMessage(3);
                    splash();
                }
            }
        });

    }

    //下载并安装证书
    private void downloadCert(String curl,final int version){
        Log.e("start","download");
        new LogicHelper(this).getInputStream(curl, new Callbacks.StreamCallback() {
            @Override
            public void onResult(boolean success, InputStream in) {
                handler.sendEmptyMessage(4);
                //如果获取证书流失败，就用默认的证书
                if(in == null){
                    try{
                        in = getAssets().open(Constants.Config.CERTIFICATE_NAME);
                        handler.sendEmptyMessage(5);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //将证书流安装进系统用户证书，返回证书alias
                CertificateProvider.prepareCertificate(getApplicationContext(),in, new CertificateProvider.CertificateResultCallback() {
                    @Override
                    public void onCertificateReady(String alias) {
                        handler.sendEmptyMessage(6);
                        CustomSharePreference.putString(getApplicationContext(),Constants.Config.CERTIFICATE_ALIAS,alias);
                        if(alias!=null){
                            Log.e("alias",alias);
                        }
                        CustomSharePreference.putInteger(getApplicationContext(),Constants.Config.CERTIFICATE_VERSION,version);
                        splash();
                    }
                });
            }
        });
    }

    /**
     * 根据情况判断是进入登录页，还是直接登录
     */
    private void splash(){
        handler.sendEmptyMessage(7);
        String username,pwd;
        username = CustomSharePreference.getString(getApplicationContext(), Constants.Flags.SP_USERNAME);
        pwd = CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PASSWORD);
        if(CustomSharePreference.getInt(getApplicationContext(),Constants.Flags.SP_REMEMBER_PASSWORD)!=0){
            pwd="";
        }
        if(Validator.isUserName(username) && Validator.checkPwd(pwd) && (CustomSharePreference.getInt(getApplicationContext(),Constants.Flags.SP_HAS_LOGOUT)!=0)){
            String deviceId = ((StrongSwanApplication)getApplication()).getANDROID_ID();
            new LogicHelper(this).login(username, pwd,deviceId, new Callbacks.LoginCallback() {
                @Override
                public void onSuccess(String token) {
                    ((StrongSwanApplication)getApplication()).setToken(token);
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(int status, String msg) {
                    login();
                }
            });
        }else{
            login();
        }
    }

    private void login(){
        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        finish();
    }
}
