package com.eju.cy.drawlibrary;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.smtt.sdk.QbSdk;

public class JDHomeSdk {


    public static void init(Context context) {

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
               ToastUtils.showShort( " 初始化结果" + arg0);
            }

            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(context, cb);
    }

}

