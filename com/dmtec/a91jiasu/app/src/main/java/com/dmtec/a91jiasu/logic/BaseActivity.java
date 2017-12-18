package com.dmtec.a91jiasu.logic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * Created by dmtec on 2017/8/17.
 * BaseActivity
 */

public class BaseActivity extends AppCompatActivity {
    public String LIFE_TIME = "INIT";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LIFE_TIME = "onCreate";
        Log.e("Activity", LIFE_TIME);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        LIFE_TIME = "onResume";
        Log.e("Activity", LIFE_TIME);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        LIFE_TIME = "onRestart";
        Log.e("Activity", LIFE_TIME);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        LIFE_TIME = "onDestory";
        Log.e("Activity", LIFE_TIME);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        LIFE_TIME = "onStart";
        Log.e("Activity", LIFE_TIME);
        super.onStart();
    }

    @Override
    protected void onPause() {
        LIFE_TIME = "onPause";
        Log.e("Activity", LIFE_TIME);
        super.onPause();
    }

    @Override
    protected void onStop() {
        LIFE_TIME = "onStop";
        Log.e("Activity", LIFE_TIME);
        super.onStop();
    }
}
