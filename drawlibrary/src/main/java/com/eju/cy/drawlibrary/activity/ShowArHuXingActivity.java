package com.eju.cy.drawlibrary.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.drawlibrary.R;
import com.eju.cy.drawlibrary.bean.RoomDataDto;
import com.eju.cy.drawlibrary.utils.AppTags;
import com.eju.cy.drawlibrary.web.AndroidInterface;
import com.eju.cy.drawlibrary.web.ArInterface;
import com.eju.cy.drawlibrary.web.X5CustomSettings;
import com.google.gson.Gson;
import com.just.agentwebX5.AgentWebX5;
import com.just.agentwebX5.DefaultWebClient;
import com.just.agentwebX5.MiddleWareWebChromeBase;
import com.just.agentwebX5.MiddleWareWebClientBase;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class ShowArHuXingActivity extends AppCompatActivity {
    private AgentWebX5 mAgentWeb;
    RelativeLayout rlView;

    private MiddleWareWebChromeBase mMiddleWareWebChrome;
    private MiddleWareWebClientBase mMiddleWareWebClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ar_hu_xing);

        rlView = (RelativeLayout) findViewById(R.id.rl_view);


        mAgentWeb = AgentWebX5.with(this)
                .setAgentWebParent(rlView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()//
                .defaultProgressBarColor()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .useMiddleWareWebChrome(getMiddleWareWebChrome())
                .useMiddleWareWebClient(getMiddleWareWebClient())
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .interceptUnkownScheme()
                .setSecutityType(AgentWebX5.SecurityType.strict)
                .setWebSettings(new X5CustomSettings())
                // .setWebLayout(new WebLayout(this))
                .createAgentWeb()//
                .ready()
                .go("file:///android_asset/huxingmobile/index.html");

        if (mAgentWeb != null) {
            //注入对象
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, this, new ArInterface() {
                @Override
                public void pushMsg(String msg) {
                    Gson gson = new Gson();
                    RoomDataDto roomDataDto = gson.fromJson(msg, RoomDataDto.class);

                    switch (roomDataDto.getMethod()) {

                        case "pageloaded"://初始化完毕

                            //room  data
                            String no = getIntent().getStringExtra(AppTags.ROOM_NO);
                            String data = getIntent().getStringExtra(AppTags.ROOM_DATA);
                            String arDrawData = getIntent().getStringExtra(AppTags.ROOM_AR_DATA);


//
                            //打开ar绘制户型
                            if (no != null && data != null && no.length() > 0 && data.length() > 10) {
                                openDrawRoom(no, data);
                            } else if (arDrawData != null && arDrawData.length() > 0) {
                                openArDrawRoom(arDrawData);
                            }


                            break;
                        case "pushCaseDataToIos"://H5上的保存按钮
                            returnRoomData(roomDataDto.getData(), true);
                            break;

                        case "back"://界面上的退出
//
//                            IsSaveRoomDataDialog defaultDialog = new IsSaveRoomDataDialog("是否保存该户型图", "如果不保存，您更改的内容将会丢失", "保存户型图", "返回，不保存", new ArDialogInterface() {
//
//
//                                @Override
//                                public void dialogCommit() {
//                                    returnRoomData(roomDataDto.getData(), false);
//                                    finish();
//                                }
//
//                                @Override
//                                public void dialogFinish(String msg) {
//                                    finish();
//                                }
//
//                                @Override
//                                public void dialogFinish() {
//                                    finish();
//                                }
//                            });
//
//                            defaultDialog.show(getSupportFragmentManager(), "defaultDialog");

                            break;

                    }


                    LogUtils.w("Msg-----------" + msg);
                }
            }));
            mAgentWeb.getJsEntraceAccess().quickCallJs("callAndroid");
        }


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_quit", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {


                        String mValue = value.replaceAll("\"", "").toString();

                        if ("true".equals(mValue)) {
                            // Timber.w("保存");


                        } else {
                            //Timber.w("退出");
                            finish();
                        }

                    }
                });


            }
        });

    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
        }
    };


    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            Log.i("Info", "BaseWebActivity onPageStarted");
        }
    };

    public MiddleWareWebChromeBase getMiddleWareWebChrome() {
        return mMiddleWareWebChrome;
    }

    public MiddleWareWebClientBase getMiddleWareWebClient() {
        return mMiddleWareWebClient;
    }


    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAgentWeb.getWebLifeCycle().onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }


    /**
     * 打开户型
     *
     * @param no
     * @param data
     */
    private void openDrawRoom(String no, String data) {

        String myData = data.replaceAll(" ", "");

        mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_openHuxing", no, data + "");

    }

    /**
     * 打开AR 绘制户型
     *
     * @param arDrawData
     */
    private void openArDrawRoom(String arDrawData) {
        LogUtils.w("Ar-Data" + arDrawData);
        mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_loadPath", arDrawData);

    }


    /**
     * 返回户型数据
     * 在需要处理业务逻辑的地方 实现EventBusTags.PUSH_ROOM_DATA 即可获得完成后的户型数据
     *
     * @param jsonString 户型数据 同以前一样
     * @param isH5Save   是否H5上面的保存按钮
     */
    public void returnRoomData(String jsonString, Boolean isH5Save) {
        // pushRoomDataCallBack.pushData(jsonString, isH5Save);
        Intent intent = new Intent(AppTags.ACTION);
        intent.putExtra(AppTags.DRAWING_ROOM_DTO, jsonString);
        intent.putExtra(AppTags.IS_H5_SAVE, isH5Save);

        sendBroadcast(intent);
        if (isH5Save == false) {
            finish();
        }
    }

}