package com.dmtec.a91jiasu.common;

import android.os.Handler;
import android.os.Message;

/**
 * Created by dmtec on 2017/8/16.
 * timer
 */

public class Timer {

    private int seconds;
    private TimerCallback callback;
    private boolean reset;
    private boolean paused;
    private boolean finished;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                callback.onTimeOut();
                finished = true;
            }else{
                callback.onTimeFailed();
            }
        }
    };

    public Timer(int seconds, TimerCallback callback){
        this.callback = callback;
        this.seconds = seconds;
        this.reset = false;
        this.paused = true;
        this.finished = false;
    }

    private Runnable tRunnable = new Runnable() {
        @Override
        public void run() {
            finished = false;
            int time = seconds;
            while(time > 0){
                while (paused){}
                while(reset){
                    time = seconds;
                    reset = false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                }
                time--;
                callback.onSecondPassed(time);
            }
            handler.sendEmptyMessage(0);
        }
    };

    public void start(){
        this.paused = false;
        new Thread(tRunnable).start();
    }

    public synchronized void reset(){
        this.reset = true;
        this.paused = false;
    }

    public synchronized void pause(){
        this.paused = true;
    }

    public synchronized void contin(){
        this.paused = false;
    }

    public boolean hasFinished(){
        return this.finished;
    }

    public  interface TimerCallback{
        void onTimeOut();
        void onTimeFailed();
        void onSecondPassed(int timeLeft);
    }
}
