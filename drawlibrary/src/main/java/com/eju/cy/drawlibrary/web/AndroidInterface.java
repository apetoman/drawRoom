package com.eju.cy.drawlibrary.web;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.just.agentwebX5.AgentWebX5;

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private AgentWebX5 agent;
    private Context context;
    private  ArInterface arInterface;

    public AndroidInterface(AgentWebX5 agent, Context context, ArInterface arInterface) {
        this.agent = agent;
        this.context = context;
        this.arInterface=arInterface;
    }

    public AndroidInterface(AgentWebX5 agent, Context context) {
        this.agent = agent;
        this.context = context;

    }

    //新版绘制户型
    @JavascriptInterface
    public void callAndroid(String msg) {
        if (msg != null && msg.length() > 0) {
            arInterface.pushMsg(msg);
        }


    }
}
