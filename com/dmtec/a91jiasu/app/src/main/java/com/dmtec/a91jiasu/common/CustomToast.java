package com.dmtec.a91jiasu.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import com.dmtec.a91jiasu.R;

import es.dmoral.toasty.Toasty;

/**
 * Created by dmtec on 2017/8/20.
 *
 */

public class CustomToast {

    private static void defaultToast(Activity activity,Context context, String msg){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        Toast toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP,0,height/80);
        toast.show();
    }


    private static void primaryToast(Activity activity,Context context, String msg){
        View root = activity.getWindow().getDecorView();
        TSnackbar snackbar = TSnackbar.make(root,msg,1050,TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackbar.addIcon(R.drawable.info_white,200,200);
        snackbar.setBackgroundColor(context.getResources().getColor(R.color.primary_btn_pressed));
        snackbar.setMessageTextColor(Color.WHITE);
        snackbar.setMessageTextSize(18);
        snackbar.show();
    }

    public static void toast(Activity activity,Context context,String msg){
        if(Constants.Config.USE_DEFAULT_TOAST){
            defaultToast(activity,context,msg);
        }else{
            primaryToast(activity,context,msg);
        }
    }

    //在其它线程里调用Toast
    public static void asynsToast(final Context context, final Activity activity, final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast(activity,context,msg);
            }
        });
    }
}