package com.example.bqmm_demo;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.melink.bqmmsdk.sdk.BQMM;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * BQMM集成
         * 首先从AndroidManifest.xml中取得appId和appSecret，然后对BQMM SDK进行初始化
         */
        try {
            Bundle bundle = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            BQMM.getInstance().initConfig(this, bundle.getString("bqmm_app_id"), bundle.getString("bqmm_app_secret"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
