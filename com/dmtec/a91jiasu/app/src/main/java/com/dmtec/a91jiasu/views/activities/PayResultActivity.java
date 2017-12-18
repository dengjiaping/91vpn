package com.dmtec.a91jiasu.views.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;

public class PayResultActivity extends AppCompatActivity {

    private boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        setResult(40001);
        success = getIntent().getBooleanExtra("PAY_RESULT",false);
        initView(success);
    }

    private void initView(boolean success){
        View back = findViewById(R.id.title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("支付结果");

        ImageView img = (ImageView)findViewById(R.id.result_img);

        TextView resultTitle = (TextView)findViewById(R.id.result_title);
        TextPaint tp = resultTitle.getPaint();
        tp.setFakeBoldText(true);

        TextView resultHint = (TextView)findViewById(R.id.result_hint);
        Button btn = (Button)findViewById(R.id.result_btn);

        if(success){
            img.setImageResource(R.drawable.pay_success);
            resultTitle.setText("订单支付成功");
            resultHint.setText("您可以尽情的享受服务了");
            btn.setText("回到首页");
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
