package com.eju.cy.drawroomsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eju.cy.drawlibrary.activity.DrawRoomActivity
import com.eju.cy.drawlibrary.activity.EnumPortActivity
import com.eju.cy.drawlibrary.activity.ShowArHuXingActivity
import com.eju.cy.drawlibrary.activity.TestActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }


    private fun initView() {

        tv_open.setOnClickListener {

            var intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }
}
