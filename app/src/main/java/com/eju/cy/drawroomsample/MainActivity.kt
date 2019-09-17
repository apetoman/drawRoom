package com.eju.cy.drawroomsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eju.cy.drawlibrary.activity.TestActivity
import com.eju.cy.drawlibrary.plug.EjuDrawEventCar
import com.eju.cy.drawlibrary.plug.EjuDrawObserver
import kotlinx.android.synthetic.main.activity_main.*
import java.util.HashMap

class MainActivity : AppCompatActivity(), EjuDrawObserver {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openUserId = "123456"//此ID为用户身份唯一标示（类似用户ID等）
        jdd_view.a(this, supportFragmentManager, openUserId)
        //jdd_view.initJddDrawRoomView(this, supportFragmentManager, openUserId)
        EjuDrawEventCar.getDefault().register(this);


    }

    override fun onResume() {
        super.onResume()
        //jdd_view.c()
        //jdd_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        //jdd_view.b()
        //jdd_view.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        //jdd_view.a()
        //jdd_view.onDestroy()
        EjuDrawEventCar.getDefault().unregister(this);


    }

    override fun update(p0: Any?) {
        Log.w(localClassName, p0 as String)
    }
}
