package com.fyang.appdetection.application;

import android.app.Application;
import android.util.Log;

import org.xutils.BuildConfig;
import org.xutils.x;

public class AndroidApplication extends Application {
    private static AndroidApplication instance;
    protected final static String tag = "AndroidApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.i(tag,"test");
        //Xutils3配置
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    public static AndroidApplication getInstance(){
        return instance;
    }
}
