package com.eju.cy.drawlibrary.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.arhuxinglibrary.bean.RoomDataDto;
import com.eju.cy.drawlibrary.R;
import com.eju.cy.drawlibrary.activity.MyRoomDataList;
import com.eju.cy.drawlibrary.bean.DrawRoomDataDto;
import com.eju.cy.drawlibrary.bean.MyRoomData;
import com.eju.cy.drawlibrary.bean.SaveRoomDto;
import com.eju.cy.drawlibrary.net.DrawRoomInterface;
import com.eju.cy.drawlibrary.pop.MoreInterface;
import com.eju.cy.drawlibrary.pop.MorePopup;
import com.eju.cy.drawlibrary.utils.JsonUtils;
import com.eju.cy.drawlibrary.utils.ParameterUtils;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class JddDrawRoomView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout rl_view;
    private ImageView ej_iv_more;
    private TextView tv_share;
    private AgentWebX5 mAgentWeb;

    private PopupWindow popupWindow;
    private Activity activity;
    private MiddleWareWebChromeBase mMiddleWareWebChrome;
    private MiddleWareWebClientBase mMiddleWareWebClient;

    private Disposable disposable;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public JddDrawRoomView(Context context) {
        this(context, null);
    }

    public JddDrawRoomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JddDrawRoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context mContext) {
        this.mContext = mContext;

        View layout = inflate(mContext, R.layout.draw_room_view_layout, this);
        rl_view = layout.findViewById(R.id.rl_view);
        ej_iv_more = layout.findViewById(R.id.ej_iv_more);
        tv_share = layout.findViewById(R.id.ej_tv_share);
        ej_iv_more.setOnClickListener(this);
        tv_share.setOnClickListener(this);


        tv_share.setVisibility(View.GONE);
        ej_iv_more.setEnabled(false);

    }


    public void setActivity(Activity activity) {
        this.activity = activity;
        initWeb(activity);


    }

    private void initWeb(Activity activity) {


        mAgentWeb = AgentWebX5.with(activity)
                .setAgentWebParent(rl_view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
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


        if (null != mAgentWeb) {


            //mAgentWeb.getJsInterfaceHolder().addJavaObject("android",new AndroidInterface(mAgentWeb,mContext));

            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, mContext, new ArInterface() {
                @Override
                public void pushMsg(String msg) {


                    // LogUtils.w("msgmsgmsgmsgmsgmsg" + msg);
                    Gson gson = new Gson();
                    RoomDataDto roomDataDto = gson.fromJson(msg, RoomDataDto.class);

                    switch (roomDataDto.getMethod()) {

                        case "pageloaded"://初始化完毕

                            getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_share.setVisibility(View.VISIBLE);
                                    ej_iv_more.setEnabled(true);
                                }
                            });


                            break;
                        case "save"://H5上的保存按钮
                            returnRoomData(roomDataDto.getData(), true);
                            break;

                        case "measure"://测距
                            LogUtils.w("去蓝牙测量");

                            break;

                    }


                }
            }));


        }


    }


    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
    }

    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
    }

    public void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        if (null != compositeDisposable) {
            compositeDisposable.clear();
        }
    }

    private void loadUrl(String rul) {

        mAgentWeb.getLoader().loadUrl(rul);
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
    public void returnRoomData(final String jsonString, Boolean isH5Save) {

//        getHandler().post(new Runnable() {
//            @Override
//            public void run() {


        //  }
//        });

        saveRoomData(jsonString, "上海大学");


    }


    private void saveRoomData(String jsonString, final String cityName) {

        final DrawRoomDataDto drawRoomDataDto = JsonUtils.fromJson(jsonString, DrawRoomDataDto.class);


        Retrofit myRetrofit = new Retrofit.Builder()
                .baseUrl("https://yun.jiandanhome.com/")
                .build();
        DrawRoomInterface request = myRetrofit.create(DrawRoomInterface.class);


        final Map<String, String> headersMap = new HashMap<>();

        headersMap.put("User-Id", "9027");
        headersMap.put("User-Token", "cbcf71e1f8538fd11a0761fc759ffc5918bc092f");
        headersMap.put("X-REQUESTED-WITH", "json");
        //  headersMap.put("Http-Plat", "JDM");


        LogUtils.w("no" + drawRoomDataDto.getNo());
        LogUtils.w("data" + drawRoomDataDto.getData());
        LogUtils.w("area" + drawRoomDataDto.getArea());


        //步骤4：创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://yun.jiandanhome.com/")
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();


        final DrawRoomInterface obRequest = retrofit.create(DrawRoomInterface.class);
        obRequest.saveDrawingRoom(headersMap,
                ParameterUtils.prepareFormData(drawRoomDataDto.getNo()),
                ParameterUtils.prepareFormData(drawRoomDataDto.getData()),
                ParameterUtils.prepareFormData(drawRoomDataDto.getArea() + "")
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap(new Function<SaveRoomDto, ObservableSource<SaveRoomDto>>() {
                    @Override
                    public ObservableSource<SaveRoomDto> apply(SaveRoomDto saveRoomDto) throws Exception {
                        return obRequest.saveDrawingRoomProperty(headersMap, ParameterUtils.prepareFormData(drawRoomDataDto.getNo()), ParameterUtils.prepareFormData(cityName));
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SaveRoomDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.w("onSubscribe");
                    }

                    @Override
                    public void onNext(SaveRoomDto saveRoomDto) {

                        LogUtils.w("onNext" + saveRoomDto.getCode());

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.w("onError" + e);
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.w("onComplete");
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ej_tv_share) {
            loadUrl("file:///android_asset/huxingmobile/index.html");


        } else if (v.getId() == R.id.ej_iv_more) {
            int[] location = new int[2];
            v.getLocationInWindow(location);

            MorePopup morePopup = new MorePopup(this.activity, new MoreInterface() {
                @Override
                public void popDraw() {
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_showDefault()");

                }

                @Override
                public void popFacade() {
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_showFacade()");
                }

                @Override
                public void pop3D() {
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_show3d()");
                }

                @Override
                public void popHistory() {


                    Intent intent = new Intent(activity, MyRoomDataList.class);
                    activity.startActivity(intent);

                }

                @Override
                public void popRepertoire() {
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_showAreaList()");
                }
            });
            morePopup.setPopupGravity(Gravity.RIGHT);

            morePopup.showPopupWindow(v);


        }
    }
}
