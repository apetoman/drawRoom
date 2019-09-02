package com.eju.cy.drawroomsample;

import android.app.Application;
import android.util.Log;

import com.eju.cy.drawlibrary.JDHomeSdk;


public class IApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        JDHomeSdk.init(getApplicationContext());

    }
}
