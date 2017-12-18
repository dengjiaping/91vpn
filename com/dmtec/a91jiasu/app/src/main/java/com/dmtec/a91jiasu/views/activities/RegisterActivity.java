package com.dmtec.a91jiasu.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.common.CustomToast;
import com.dmtec.a91jiasu.common.Timer;
import com.dmtec.a91jiasu.common.Validator;
import com.dmtec.a91jiasu.logic.BaseActivity;
import com.dmtec.a91jiasu.logic.Callbacks;
import com.dmtec.a91jiasu.logic.LogicHelper;
import com.dmtec.a91jiasu.ui.MainActivity;
import com.dmtec.a91jiasu.ui.StrongSwanApplication;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private View contactArea,toolbarBack;
    private LogicHelper logicHelper;
    private Button codeBtn,regBtn;
    private TextView usernameTV,codeTV,pwdTV,rePwdTV,userPro;

    private Timer timer = new Timer(Constants.Config.SMS_CODE_TIMEOUT, new Timer.TimerCallback() {
        @Override
        public void onTimeOut() {
            resetCodeBtn();
        }

        @Override
        public void onTimeFailed() {

        }

        @Override
        public void onSecondPassed(final int timeLeft) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String str = timeLeft + "S";
                    codeBtn.setText(str);
                }
            });
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        logicHelper = new LogicHelper(this);
        initView();
    }


    private void initView(){
        contactArea = findViewById(R.id.contact_area);
        contactArea.setOnClickListener(this);
        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarBack.setOnClickListener(this);
        codeBtn = (Button)findViewById(R.id.button);
        codeBtn.setOnClickListener(this);
        regBtn = (Button)findViewById(R.id.register_btn_register);
        regBtn.setOnClickListener(this);

        usernameTV = (TextView)findViewById(R.id.register_ed_username);
        codeTV = (TextView)findViewById(R.id.register_ed_code);
        pwdTV = (TextView)findViewById(R.id.register_ed_pwd);
        rePwdTV = (TextView)findViewById(R.id.register_ed_confirm_pwd);

        userPro = (TextView)findViewById(R.id.user_pro);
        userPro.setOnClickListener(this);
    }

    /**
     * 获取验证码
     */
    private void processCode(){
        codeBtn.setEnabled(false);
        final String num = usernameTV.getText().toString();
        if(!Validator.isUserName(num)){
            CustomToast.toast(RegisterActivity.this,getApplicationContext(),"用户名格式不正确");
            resetCodeBtn();
        }else{
            //1 验证手机号是否注册
            logicHelper.checkAccount(num, "username", new Callbacks.CheckAccountCallback() {
                @Override
                public void onResult(boolean success, String msg) {
                    if(!success){
                        CustomToast.asynsToast(getApplicationContext(),RegisterActivity.this,msg);
                        resetCodeBtn();
                    }else{
                        //2 请求发送验证码
                        logicHelper.requestSmsCode(num, "regist", new Callbacks.RequestSmsCodeCallback() {
                            @Override
                            public void onResult(boolean success, int status,String msg) {
                                if(!success){
                                    CustomToast.asynsToast(getApplicationContext(),RegisterActivity.this,msg);
                                    resetCodeBtn();
                                }else{
                                    CustomToast.asynsToast(getApplicationContext(),RegisterActivity.this,msg);
                                    timer.start();//开始倒计时
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 注册
     */
    private void register(){
        final String account = usernameTV.getText().toString();
        final String code = codeTV.getText().toString();
        final String pwd = pwdTV.getText().toString();
        final String repwd = rePwdTV.getText().toString();
        String msg = null;
        if(!Validator.isUserName(account)){
            msg = "用户名格式错误";
            if(account.length() == 0){
                msg = "请输入正确的用户名";
            }
        }else if(!Validator.isSmsCode(code)){
            msg = "验证码格式不正确";
        }else if(!Validator.checkPwd(pwd)){
            msg="密码长度6-30位";
        }else if(!pwd.equals(repwd)){
            msg = "两次密码不一致";
        }else{
            regBtn.setEnabled(false);
            logicHelper.register(account, pwd, code, new Callbacks.RegisterCallback() {
                @Override
                public void onResult(boolean success, int status,final String msg) {
                    if(!success){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                regBtn.setEnabled(true);
                                CustomToast.toast(RegisterActivity.this,getApplicationContext(),msg);
                            }
                        });
                    }else{
                        CustomToast.asynsToast(getApplicationContext(),RegisterActivity.this,"注册成功，请稍候");
                        String deviceId = ((StrongSwanApplication)getApplication()).getANDROID_ID();
                        logicHelper.login(account, pwd,deviceId, new Callbacks.LoginCallback() {
                            @Override
                            public void onSuccess(String token) {
                                ((StrongSwanApplication)getApplication()).setToken(token);
                                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_USERNAME,account);
                                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_PASSWORD,pwd);
                                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onFailure(int status, String msg) {
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                ((StrongSwanApplication)getApplication()).vpnPut(Constants.Flags.SP_USERNAME,account);
                                ((StrongSwanApplication)getApplication()).vpnPut(Constants.Flags.SP_PASSWORD,pwd);
                                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_USERNAME,account);
                                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_PASSWORD,pwd);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            });
        }
        if(msg != null){
            CustomToast.toast(RegisterActivity.this,getApplicationContext(),msg);
        }
    }

    /**
     * 重设验证码按钮
     */
    private void resetCodeBtn(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                codeBtn.setEnabled(true);
                codeBtn.setText("获取验证码");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.contact_area:
                startActivity(new Intent(RegisterActivity.this, ContactActivity.class));
                break;
            case R.id.button:
                processCode();
                break;
            case R.id.register_btn_register:
                register();
                break;
            case R.id.user_pro:
                Intent intent = new Intent(RegisterActivity.this,WebActivity.class);
                intent.putExtra(Constants.Flags.INTENT_URL, Constants.getUserProUrl());
                intent.putExtra(Constants.Flags.INTENT_USER_PRO,true);
                startActivity(intent);
                break;
        }
    }
}
