package com.eju.cy.drawlibrary;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.smtt.sdk.QbSdk;

public class IApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();



        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.w(  "腾讯X5初始化结果" + arg0);


            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }
}