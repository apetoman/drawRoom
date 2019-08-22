package com.eju.cy.drawlibrary.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.eju.cy.drawlibrary.R;
import com.eju.cy.drawlibrary.view.JddDrawRoomView;

public class TestActivity extends AppCompatActivity {
    JddDrawRoomView jddView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        jddView = findViewById(R.id.jdd_view);
        jddView.setActivity(this);

    }
}
