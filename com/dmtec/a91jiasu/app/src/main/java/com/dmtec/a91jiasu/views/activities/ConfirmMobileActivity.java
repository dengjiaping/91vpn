package com.dmtec.a91jiasu.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.dmtec.a91jiasu.ui.StrongSwanApplication;

public class ConfirmMobileActivity extends BaseActivity implements View.OnClickListener {

    private Button setNewPwd, codeBtn;
    private View toolbarBack;
    private EditText usernameET, codeET;
    private LogicHelper logicHelper;

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
        setContentView(R.layout.activity_confirm_mobile);
        logicHelper = new LogicHelper(this);
        initView();

    }

    private void initView() {
        ((TextView) findViewById(R.id.title)).setText("账号验证");
        setNewPwd = (Button) findViewById(R.id.set_new_pwd);
        setNewPwd.setOnClickListener(this);
        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarBack.setOnClickListener(this);
        codeBtn = (Button) findViewById(R.id.button);
        codeBtn.setOnClickListener(this);
        usernameET = (EditText) findViewById(R.id.register_ed_username);
        logicHelper.requestFocus(usernameET);
        codeET = (EditText) findViewById(R.id.register_ed_code);
        ;
        if (getIntent().getBooleanExtra(Constants.Flags.INTENT_CHANGE_PASSWORD, false)) {
            String username = CustomSharePreference.getString(getApplicationContext(), "username");
            usernameET.setText(username);
            usernameET.setEnabled(false);
            logicHelper.requestFocus(codeET);
        }
    }

    private void processCode() {
        codeBtn.setEnabled(false);
        final String username = usernameET.getText().toString();
        if (!Validator.isUserName(username)) {
            CustomToast.toast(ConfirmMobileActivity.this, getApplicationContext(), "用户名格式不正确");
            resetCodeBtn();
        } else {
            logicHelper.requestSmsCode(username, "change", new Callbacks.RequestSmsCodeCallback() {
                @Override
                public void onResult(boolean success, int status, String msg) {
                    if (!success) {
                        CustomToast.asynsToast(getApplicationContext(), ConfirmMobileActivity.this, msg);
                        resetCodeBtn();
                    } else {
                        CustomToast.asynsToast(getApplicationContext(), ConfirmMobileActivity.this, msg);
                        timer.start();
                    }
                }
            });
        }
    }

    /**
     * 验证验证码
     */
    private void next() {
        final String username = usernameET.getText().toString();
        final String code = codeET.getText().toString();
        String msg = null;
        setNewPwd.setEnabled(false);
        if (!Validator.isUserName(username)) {
            msg = "用户名格式不正确";
        } else if (!Validator.isSmsCode(code)) {
            msg = "验证码错误";
        } else {
            logicHelper.checkAppVerify(username, code, new Callbacks.CheckAppVerifyCallback() {
                @Override
                public void onSuccess(String uid, String username) {
                    Intent intent = new Intent(ConfirmMobileActivity.this, SetPwdActivity.class);
                    ((StrongSwanApplication) getApplication()).vpnPut("username", username);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String msg) {
                    CustomToast.asynsToast(getApplicationContext(), ConfirmMobileActivity.this, msg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNewPwd.setEnabled(true);
                        }
                    });
                }
            });
        }
        if (msg != null) {
            CustomToast.toast(ConfirmMobileActivity.this, getApplicationContext(), msg);
        }
        setNewPwd.setEnabled(true);
    }

    /**
     * 重设验证码按钮
     */
    private void resetCodeBtn() {
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
        switch (v.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.set_new_pwd:
                next();
                break;
            case R.id.button:
                processCode();
                break;
        }
    }
}
