package com.itheima.bdmdemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * 创建者:   Leon
 * 创建时间:  2016/11/21 17:16
 * 描述：    TODO
 */
public class BDMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
