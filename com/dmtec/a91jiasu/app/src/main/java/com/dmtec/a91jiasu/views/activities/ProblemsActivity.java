package com.dmtec.a91jiasu.views.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.logic.BaseActivity;

public class ProblemsActivity extends BaseActivity implements View.OnClickListener{

    private View back,q1,q2,q3;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problems);

        initView();
    }

    private void initView(){
        back = findViewById(R.id.toolbar_back);
        back.setOnClickListener(this);

        title = (TextView)findViewById(R.id.title);
        title.setText("常见问题");

        q1 = findViewById(R.id.how_to_use);
        q1.setOnClickListener(this);
        q2 = findViewById(R.id.fail_to_connect);
        q2.setOnClickListener(this);
        q3 = findViewById(R.id.stop_kill);
        q3.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent  = new Intent(ProblemsActivity.this,ProblemDetailActivity.class);
        switch (v.getId()){
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.how_to_use:
                intent.putExtra("problem_id",0);
                startActivity(intent);
                break;
            case R.id.fail_to_connect:
                intent.putExtra("problem_id",1);
                startActivity(intent);
                break;
            case R.id.stop_kill:
                intent.putExtra("problem_id",2);
                startActivity(intent);
                break;
        }
    }
}
