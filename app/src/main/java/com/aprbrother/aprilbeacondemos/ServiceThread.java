package com.aprbrother.aprilbeacondemos;

import android.os.Handler;

/**
 * Created by hp on 2017-09-19.
 */

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;

    public ServiceThread(Handler handler){
        this.handler = handler;
    }
    public void stopForever(){
        synchronized (this){
            this.isRun=false;
        }
    }
    public void run(){
        while(isRun){
            handler.sendEmptyMessage(0);
            try{
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
