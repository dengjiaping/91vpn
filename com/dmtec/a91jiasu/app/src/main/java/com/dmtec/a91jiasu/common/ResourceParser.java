package com.dmtec.a91jiasu.common;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dmtec on 2017/7/31.
 * Provides methods to parse resources from one file type to another;
 */

public class ResourceParser {

    //Bitmap to File
    public static  File bitmap2file(Bitmap bitmap, String dir){
        FileOutputStream fos;
        try{
            fos = new FileOutputStream(dir);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.close();
            return new File(dir);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //Drawable to Bitmap
    public static Bitmap drawable2bitmap(Drawable drawable){
        return ((BitmapDrawable)drawable).getBitmap();
    }

    //Drawable to File
    public static File drawable2file(Drawable drawable, String filename){
        return bitmap2file(drawable2bitmap(drawable),filename);
    }
}
