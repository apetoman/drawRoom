package com.eju.cy.drawlibrary.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.drawlibrary.R;
import com.eju.cy.drawlibrary.plug.EjuDrawEventCar;
import com.eju.cy.drawlibrary.plug.EjuDrawObserver;
import com.eju.cy.drawlibrary.view.JddDrawRoomView;

public class TestActivity extends AppCompatActivity implements EjuDrawObserver {
    JddDrawRoomView jddView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        jddView = findViewById(R.id.jdd_view);
        jddView.initJddDrawRoomView(this, getSupportFragmentManager());
        EjuDrawEventCar.getDefault().register(this);


    }

    @Override
    public void update(Object obj) {
        LogUtils.w("我接收到的是" + (String) obj);

    }

    @Override
    protected void onPause() {
        super.onPause();
        jddView.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        jddView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EjuDrawEventCar.getDefault().unregister(this);
        jddView.onDestroy();

    }
}
