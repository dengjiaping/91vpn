package com.dmtec.a91jiasu.views.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomToast;
import com.dmtec.a91jiasu.logic.BaseActivity;
import com.dmtec.a91jiasu.logic.Payment;
import com.dmtec.a91jiasu.models.Plan;
import com.dmtec.a91jiasu.ui.MainActivity;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class PayActivity extends BaseActivity implements View.OnClickListener{

    private Plan plan;
    private View aliItem,wxItem,titleBack;
    private ImageView aliImg,wxImg;
    private TextView aliTitle,wxTitle,planName,planDesc,planDur,planCost;
    private CheckBox cbxAli,cbxWx;
    private Button pay;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
        initData();
    }

    private void initView(){

        titleBack = findViewById(R.id.title_back);
        titleBack.setOnClickListener(this);

        ((TextView)findViewById(R.id.title)).setText("确认订单信息");

        planName = (TextView)findViewById(R.id.plan_name);
        planDesc = (TextView)findViewById(R.id.plan_desc);
        planDur = (TextView)findViewById(R.id.plan_expire);
        planCost = (TextView)findViewById(R.id.plan_cost);

        aliItem = findViewById(R.id.ali_item);
        wxItem = findViewById(R.id.wx_item);
        aliItem.setOnClickListener(this);
        wxItem.setOnClickListener(this);

        aliImg = (ImageView)aliItem.findViewById(R.id.item_img);
        aliImg.setImageResource(R.drawable.ali_pay);
        wxImg = (ImageView)wxItem.findViewById(R.id.item_img);
        wxImg.setImageResource(R.drawable.wx_pay);

        aliTitle = (TextView)aliItem.findViewById(R.id.item_name);
        aliTitle.setText("支付宝支付");
        wxTitle = (TextView)wxItem.findViewById(R.id.item_name);
        wxTitle.setText("微信支付");

        cbxAli = (CheckBox)aliItem.findViewById(R.id.pay_checkbox);
        cbxWx = (CheckBox)wxItem.findViewById(R.id.pay_checkbox);
        cbxAli.setClickable(false);
        cbxWx.setClickable(false);

        pay = (Button)findViewById(R.id.app_pay);
        pay.setOnClickListener(this);

        aliItem.callOnClick();
    }

    private  void initData(){
        Object obj = getIntent().getSerializableExtra("MAIN.ACTIVITY.LIST.PLAN");
        plan = (obj!=null) ? (Plan)obj:null;
        if(plan!=null){
            planName.setText(plan.getName());
            planDesc.setText(plan.getDesc());
            planDur.setText(plan.getDuration()+"天");
            planCost.setText(plan.getCost()+"RMB");
        }
    }

    private void pay(boolean wxPay){
        Log.e("wxPay",wxPay+"");
        Payment payment = new Payment(plan);
        /**
         * *************************************
         *  修改实际支付方式
         * *************************************
         */
        success = payment.pay(wxPay);
        showResult();
    }

    private void showResult(){
        Intent intent = new Intent(PayActivity.this,PayResultActivity.class);
        intent.putExtra("PAY_RESULT",success);
        startActivityForResult(intent,40000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.ali_item:
                cbxAli.setChecked(true);
                cbxWx.setChecked(false);
                break;
            case R.id.wx_item:
                cbxAli.setChecked(false);
                cbxWx.setChecked(true);
                break;
            case R.id.app_pay:
                pay(cbxWx.isChecked());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 40000 && resultCode == 40001){
            Intent intent = new Intent(PayActivity.this,MainActivity.class);
            intent.putExtra(Constants.Flags.AR_PAY_SUCCESS,success);
            this.setResult(50001,intent);
            finish();
        }else{
            this.setResult(50001);
            finish();
        }
    }
}
