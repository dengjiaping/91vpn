package com.dmtec.a91jiasu.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.dmtec.a91jiasu.ui.StrongSwanApplication;

public class SetPwdActivity extends BaseActivity implements View.OnClickListener {

    private View contactArea, toolbarBack;
    private EditText pwd,rePwd;
    private Button btn;

    private LogicHelper logicHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        logicHelper = new LogicHelper(this);
        initView();
    }

    private void initView(){
        ((TextView)findViewById(R.id.title)).setText("设置新密码");
        contactArea = findViewById(R.id.contact_area);
        contactArea.setOnClickListener(this);
        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarBack.setOnClickListener(this);

        pwd = (EditText)findViewById(R.id.register_ed_pwd);
        rePwd = (EditText)findViewById(R.id.register_ed_confirm_pwd);
        btn = (Button)findViewById(R.id.btn_set_new_pwd);
        btn.setOnClickListener(this);
    }

    private void setPas(){
        final String p = pwd.getText().toString();
        final String rp = rePwd.getText().toString();
        final String username = (String)((StrongSwanApplication)getApplication()).vpnGet("username");
        if(!p.equals(rp)){
            CustomToast.toast(SetPwdActivity.this,getApplicationContext(),"两次密码不一致");
        }else if(!Validator.checkPwd(p)){
            CustomToast.toast(SetPwdActivity.this,getApplicationContext(),"请设置6-30位密码");
        }else{
            btn.setEnabled(false);
            logicHelper.setPassword(username, p, new Callbacks.RequestCallback() {
                @Override
                public void onResult(boolean success, int status) {
                    if(!success){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomToast.toast(SetPwdActivity.this,getApplicationContext(),"密码设置失败，请稍后尝试");
                                btn.setEnabled(true);
                            }
                        });
                    }else{
                        CustomSharePreference.putString(getApplicationContext(), Constants.Flags.SP_PASSWORD,p);
                        ((StrongSwanApplication)getApplication()).vpnPut(Constants.Flags.SP_PASSWORD,p);
                        CustomToast.asynsToast(getApplicationContext(),SetPwdActivity.this,"密码设置成功");
                        finish();
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.contact_area:
                startActivity(new Intent(SetPwdActivity.this,ContactActivity.class));
                break;
            case R.id.btn_set_new_pwd:
                setPas();
                break;
        }
    }
}
