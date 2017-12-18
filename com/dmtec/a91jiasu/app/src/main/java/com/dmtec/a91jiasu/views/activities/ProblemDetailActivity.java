package com.dmtec.a91jiasu.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.ResourceParser;
import com.dmtec.a91jiasu.logic.BaseActivity;

import java.io.InputStream;

public class ProblemDetailActivity extends BaseActivity implements View.OnClickListener{
    private View back,v1,v2,v3;
    private TextView title;
    private int problemId;
    private ImageView s1,s2,s3,s4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);
        problemId = getIntent().getIntExtra("problem_id", 0);
        initView();
    }

    private void initView(){
        back = findViewById(R.id.toolbar_back);
        back.setOnClickListener(this);

        title = (TextView)findViewById(R.id.title);
        title.setText("问题详情");

        v1 = findViewById(R.id.problem1);
        v2 = findViewById(R.id.problem2);
        v3 = findViewById(R.id.problem3);

        s1 = (ImageView)findViewById(R.id.step1);
        s2 = (ImageView)findViewById(R.id.step2);
        s3 = (ImageView)findViewById(R.id.step3);
        s4 = (ImageView)findViewById(R.id.step4);

        setPics(s1, R.drawable.step1);
        setPics(s2, R.drawable.step2);
        setPics(s3, R.drawable.step3);
        setPics(s4, R.drawable.step4);

        showProblem(problemId);
    }


    private void showProblem(int id){
        v1.setVisibility(View.GONE);
        v2.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
        if(id == 0){
            v1.setVisibility(View.VISIBLE);
        }else if(id == 1){
            v2.setVisibility(View.VISIBLE);
        }else{
            v3.setVisibility(View.VISIBLE);
        }
    }
    private void setPics(ImageView imageview,int drawableId){
        WindowManager wm = this.getWindowManager();
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int w = 1080;
        int h = 1720;
        int y = screenWidth * h / w;
        imageview.getLayoutParams().height = y;
        imageview.getLayoutParams().width = screenWidth;
        imageview.invalidate();
    }

    private Bitmap readBitMap(int resid){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 8;
        InputStream is = getResources().openRawResource(resid);
        return BitmapFactory.decodeStream(is,null,opt);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back:
                finish();
                break;
        }
    }
}
