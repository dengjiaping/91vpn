package com.dmtec.a91jiasu.views.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.common.CustomToast;
import com.dmtec.a91jiasu.common.Validator;
import com.dmtec.a91jiasu.logic.BaseActivity;
import com.dmtec.a91jiasu.logic.Callbacks;
import com.dmtec.a91jiasu.logic.LogicHelper;
import com.dmtec.a91jiasu.ui.MainActivity;
import com.dmtec.a91jiasu.ui.StrongSwanApplication;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Button btnRegister,btnLogin;
    private TextView retPwd;
    private  View contactArea,checkArea;
    private EditText eTUsername,eTPassword;
    private LogicHelper logicHelper;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        logicHelper = new LogicHelper(this);

        initView();

    }

    private void initView(){
        btnRegister = (Button)findViewById(R.id.login_btn_register);
        btnRegister.setOnClickListener(this);
        btnLogin = (Button)findViewById(R.id.login_btn_login);
        btnLogin.setOnClickListener(this);
        retPwd = (TextView)findViewById(R.id.ret_pwd);
        retPwd.setOnClickListener(this);
        contactArea = findViewById(R.id.contact_area);
        contactArea.setOnClickListener(this);

        checkBox = (CheckBox)findViewById(R.id.cbx_rmp);
        int c = CustomSharePreference.getInt(getApplicationContext(), Constants.Flags.SP_REMEMBER_PASSWORD);
        checkBox.setChecked(!(c == 1));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String pwd = eTPassword.getText().toString();
                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_PASSWORD,pwd);
            }
        });
        checkArea = findViewById(R.id.check_area);
        checkArea.setOnClickListener(this);

        eTUsername = (EditText)findViewById(R.id.login_ed_username);
        eTPassword = (EditText)findViewById(R.id.login_ed_password);
        eTUsername.setText(CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_USERNAME));
        if(c == 0){
            eTPassword.setText(CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PASSWORD));
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_btn_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.login_btn_login:
                login();
                break;
            case R.id.ret_pwd:
                startActivity(new Intent(LoginActivity.this,ConfirmMobileActivity.class));
                break;
            case R.id.contact_area:
                startActivity(new Intent(LoginActivity.this, ContactActivity.class));
                break;
            case R.id.check_area:
                boolean checked = checkBox.isChecked();
                checkBox.setChecked(!checked);
                break;
        }
    }

    //登录完毕跳转主页
    private void toMain(final String token){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((StrongSwanApplication) getApplication()).setToken(token);//store token
                CustomSharePreference.putInteger(getApplicationContext(),Constants.Flags.SP_HAS_LOGOUT,1);
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        });
    }


    //若在其它设备上登录，可以让用户选择是否顶掉其它设备
    private void whetherForceLogin(int status, String msg, final String username, final String pwd){
        //顶掉 msg=token
        if(status == 2){
            final String token = msg;
            btnLogin.setEnabled(true);
            btnLogin.setText("登录");
            String info = getResources().getString(R.string.force_login);
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setMessage(info);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btnLogin.setText("登录中...");
                    btnLogin.setEnabled(false);
                    logicHelper.forceLogin(token,username,pwd, new Callbacks.RequestCallback() {
                        @Override
                        public void onResult(boolean success, int status) {
                            if(success){
                                toMain(token);
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnLogin.setEnabled(true);
                                        btnLogin.setText("登录");
                                    }
                                });
                                CustomToast.asynsToast(getApplicationContext(),LoginActivity.this,"登录失败，请稍后再试");
                            }
                        }
                    });
                }
            });
            dialog.setNegativeButton("取消",null);
            dialog.show();
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("登录");
                }
            });
            CustomToast.asynsToast(getApplicationContext(),LoginActivity.this,msg);
        }
    }

    private void login(){
        final String username = eTUsername.getText().toString();
        final String pwd = eTPassword.getText().toString();
        if(Validator.isUserName(username) && Validator.checkPwd(pwd)){
            btnLogin.setText("登录中...");
            btnLogin.setEnabled(false);
            //记住用户名密码
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_USERNAME,username);
                    CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_PASSWORD,pwd);
                    CustomSharePreference.putInteger(getApplicationContext(),Constants.Flags.SP_REMEMBER_PASSWORD,checkBox.isChecked()?0:1);
                }
            }).start();
            String deviceId = ((StrongSwanApplication)getApplication()).getANDROID_ID();
            //登录程序
            logicHelper.login(username, pwd,deviceId, new Callbacks.LoginCallback() {
                @Override
                public void onSuccess(final String token) {
                    toMain(token);
                }

                @Override
                public void onFailure(final int status, final String msg) {
                    Log.e("status: ", status+" "+msg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            whetherForceLogin(status,msg,username,pwd);
                        }
                    });

                }
            });
        }else{
            String errMsg = "用户名或密码格式错误";
            if(pwd.length() == 0){
                errMsg = "请输入密码";
            }
            if(username.length() == 0){
                errMsg = "请输入用户名";
            }

            CustomToast.toast(LoginActivity.this,getApplicationContext(),errMsg);
        }
    }
}
