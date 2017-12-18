package com.dmtec.a91jiasu.views.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.logic.BaseActivity;
import com.dmtec.a91jiasu.logic.Callbacks;
import com.dmtec.a91jiasu.logic.LogicHelper;
import com.dmtec.a91jiasu.ui.MainActivity;

public class ContactActivity extends BaseActivity implements View.OnClickListener {

    private View toolbarBack;
    private TextView phone,qq;
    private ImageView pv,qv;
    private LogicHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        helper = new LogicHelper(this);

        initView();

        loadInfo();
    }

    private void initView(){
        ((TextView)findViewById(R.id.title)).setText("联系客服");
        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarBack.setOnClickListener(this);

        phone = (TextView)findViewById(R.id.tv_phone_num);
        qq = (TextView)findViewById(R.id.tv_contact_qq);
        pv = (ImageView)findViewById(R.id.call_action);
        qv = (ImageView)findViewById(R.id.qq_action);
        pv.setOnClickListener(this);
        qv.setOnClickListener(this);
        String q = CustomSharePreference.getString(getApplicationContext(), Constants.Flags.SP_QQ);
        String p = CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PHONE_CN);
        if((q != null) && (p != null)){
            qq.setText(q);
            phone.setText(p);
        }
    }

    private void loadInfo(){
        String q = CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_QQ);
        String p = CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PHONE_CN);
        if((q != null) && (p != null)){
            qq.setText(q);
            phone.setText(p);
        }
        helper.getService(new Callbacks.ServiceCallback() {
            @Override
            public void onSuccess(final String qqNum, final String phoneNum,final String phonecn) {
                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_QQ,qqNum);
                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_PHONE,phoneNum);
                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_PHONE_CN,phonecn);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qq.setText(qqNum);
                        phone.setText(phonecn);
                    }
                });
            }
            @Override
            public void onFailure(final String msg) {}
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.call_action:
                call();
                break;
            case R.id.qq_action:
                helper.chat(qq.getText().toString());
                break;
        }
    }

    private void call(){
        final String p =  CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PHONE);
        final String pcn =  CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PHONE_CN);
        final String ps[] ={
                "境外用户：" + p,
                "境内用户：" + pcn
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择要拨打的客服电话");

        builder.setItems(ps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                helper.call(which==0?p:pcn);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
