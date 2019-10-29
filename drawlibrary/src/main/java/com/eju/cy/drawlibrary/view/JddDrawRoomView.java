package com.eju.cy.drawlibrary.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.drawlibrary.BuildConfig;
import com.eju.cy.drawlibrary.R;
import com.eju.cy.drawlibrary.activity.EnumPortActivity;
import com.eju.cy.drawlibrary.activity.MyRoomDataList;
import com.eju.cy.drawlibrary.bean.DrawRoomDataDto;
import com.eju.cy.drawlibrary.bean.IsShowBarDto;
import com.eju.cy.drawlibrary.bean.OpenRoomDto;
import com.eju.cy.drawlibrary.bean.OpenYunDto;
import com.eju.cy.drawlibrary.bean.ResultDto;
import com.eju.cy.drawlibrary.bean.RoomDataDto;
import com.eju.cy.drawlibrary.bean.SaveRoomDto;
import com.eju.cy.drawlibrary.bluetooth.ACSUtility;
import com.eju.cy.drawlibrary.dialog.CreateConstructionNameDalog;
import com.eju.cy.drawlibrary.dialog.DeleteRoomDalog;
import com.eju.cy.drawlibrary.net.DrawRoomInterface;
import com.eju.cy.drawlibrary.net.RetrofitManager;
import com.eju.cy.drawlibrary.plug.DialogInterface;
import com.eju.cy.drawlibrary.plug.EjuDrawBleEventCar;
import com.eju.cy.drawlibrary.plug.EjuDrawEventCar;
import com.eju.cy.drawlibrary.plug.EjuDrawObserver;
import com.eju.cy.drawlibrary.pop.MoreInterface;
import com.eju.cy.drawlibrary.pop.MorePopup;
import com.eju.cy.drawlibrary.utils.ButtonUtils;
import com.eju.cy.drawlibrary.utils.JsonUtils;
import com.eju.cy.drawlibrary.utils.ParameterUtils;
import com.eju.cy.drawlibrary.utils.SharedPreferencesUtils;
import com.eju.cy.drawlibrary.web.AndroidInterface;
import com.eju.cy.drawlibrary.web.ArInterface;
import com.eju.cy.drawlibrary.web.X5CustomSettings;
import com.google.gson.Gson;
import com.jhome.util.JhomeApiException;
import com.jhome.util.JhomeConstants;
import com.jhome.util.JhomeSignature;
import com.just.agentwebX5.AgentWebX5;
import com.just.agentwebX5.DefaultWebClient;
import com.just.agentwebX5.MiddleWareWebChromeBase;
import com.just.agentwebX5.MiddleWareWebClientBase;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.blankj.utilcode.util.LogUtils.*;

/**
 * @ Name: Caochen
 * @ Date: 2019-08-19
 * @ Time: 10:54
 * @ Description： 户型绘制View
 */
public class JddDrawRoomView extends RelativeLayout implements View.OnClickListener, EjuDrawObserver {


//   private final String APP_Id = "NGRU1AVC846EQSTB";
//    private final String COMPANY_ID = "52";
//    private final String APPPRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCvUuJnHIlvUDHRcaQlcBkrpiexTUgO5aKpVoABLwpNbnHFDoLGwMXeOMwhykAFqNpxtUp8m5d+XZRdSJo5zfpMsuLzq4o4QKm8EHMQkiBuynUJxTpDbyw5rSRFGYbu83iGeTA+y+mPh9RJnyR3uG2BWk6W12n9xnzhY53BStR63fKcKs+li1lT7DvRiU8vv+aHYQNRyu8wlLmiMDa4TFx4AKBM6r2r9ZMVrMhiK+qrtsMVWOvXmNQeoiGe4pwqFwPTAQi+SSiS6mWBHeTHyTwUwr2rSyZaTudd3vLLQJpd2O1i1HVKaRnmK0Ixy/yhmWO9fH8+P+e0Rq7WSx0d0TB3AgMBAAECggEBAJf7leAk0M76CfWyOcVqg6dfBhGhGNIxJuz820IrcRbmoyFcDuoUunKFcg/or884LQVdTxDuIEme/bpP8cIiWNScTjlFfzB8fadV6yl2Qz9HqmWp33QNr5zgBw0Pr/T8goKwE66cPf/6k4CuwII4ElWL34zLeEpSAnewT1T8dW17//Xi2hy7ccFRITC9gVWnwWt2AFCvswpgodCskpboNML8YoRPzmQuy90RWeJ7Tk158jeNCYfcXyLlApvSPobAx9Nu/6D5XSbjfKoARr8b9azyC5r0RDzZF5B/ypQictnKHz/91lTtEU20RW3PN040bISskOO6vVCzQgkOW6wwBOECgYEAvXTZO7/K7uxYhBeM2tnNO/3mcaI433qBIygrnN1Y+bsGqa84im41N6xXGZr+J6RjkbLkSrjo1TYLQ1CYgf1vmklZx5ilQRdIbWBNpvYQAffDNFxYULXxzW9lND0IbNFni+/I4LjzUEmg6BW5/eXdx5PIWqbhna7jxtcpdDa/QYUCgYEA7OdIkMf35sTuInVWVSOsTZ/+axVj3b0Ecku9Ggdi3iwzGAMSmax4PcxNDcRly8jxCJtTZ9Wbr9Iqltp/Q3a4G/W7knkbSbuLUIyKcqIlIqQ33ClwwaFGiG6GBeoRJEhzW9D3HLrUJy51ZMCvEoSGhBhudCZEYih+xxCPLCmeDMsCgYAhj0Y/wDyZUAJp+6X2ymgBfXtJm7vJUnD3olD/a3IsYoXOnvw8AUOqBfwzy/HDYepFT9QCrHiJ9BXQqcEqHZOcV+vwYEi9m/s3bLy0m5fAUXwhlU4Llf8sLdRWiY0pgXp/Hk2OCRUIntJC6j5VDFfZ14LBFBiZDvbILSrprBz65QKBgQDEcHfMjfQy5+Lqsc9Xo8/xQhTOKJt5t41jVQhF+A/0WEQ5yfp3cPr3i1vtaYhbdZDgaSO8+vQw0527HwzeHShHDvltWHzXI+s+bHs02NzgH7muFrLH7Ho3ESaS6ucx5d26KcluikD3CGARnnDNcxSzniqgp0aW+is9165QmWXUBwKBgFq6jEAq+2kDcgjKam97bhsmg9eOsHIqYQuHQy6bNScxC6Amgg0Re1UzMvPUF0YHjw4JnpvLXzZmUYiN/7WVMTsChhIlWK49aVlgn0HgnS94fpNtxZwHi8HXW61i5LTsQoR7AUSZ2Uq+JM2DMAewcw49Bda2TQHxZcPu0Zts5zKQ";


    private final String APP_Id = "B7V0YDWLFMXK6HPI";
    private final String COMPANY_ID = "77";
    private final String APPPRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDbI0Odb88C28eTdNuBD1Io5UKIuk+YOvqe/RbsYhUlI8rGLamd2VnJt/eGdnyzKdi2ZGAZ06EDbtfgv2ZpI1BXxgd89vXPU9u1JzN5LjxVM7ZNy0bugT8+oW82NVsQW8JHdWKWwe6Z6L5Dwdpx7O16C7FL0E3V0iOiuFDa1UCGwuuoZZ8r9cJMhP74UzeDhUX9zKZNdFSH9K4jvylWi7oSZtaBLj2/PCrBkqYTLm9lQr1HvmqpKV7XHYjDbh5lkQZepnBQlyARJYQMparJ5TZ8jXQYAC2NAnRpEXhQr3rCCjdfakGPZiBgkudUh3WucUi4EOqWDzVPQ8QCCKCrEz5JAgMBAAECggEABRsaPTXYtfFXAhmL78LioC9PUI4+woZFRqUQ/qr+YSInXFy+Af6JeAjATLLADxCfiwhlsNRcgQ75GrR692IqQhFMJV4/tff3O45RRbaNE1MkIUXmPjJmvdfXFoVBvOzWZwHWLTsRiyaAIGYI5/SkR96z39dXqO6/g9AVmjOz0C/5bSvgnC0083tYjVMF8kBokxfOlczsBYPZiu+ZIQqkrtXvA6yCet+UMhYbXDhWcPbvVjUmKThNhY4AucCxDiqYW45DNYsyHgVePgGgzluBDkEPffdxeulMJT7jXmGj8V7uswCN+ScigEQlo737twtcChoaueGpiW/vbHkRaoHKTQKBgQDfQrLKUKEF3XGBXzgJpsYQKbAriAOYmZLzDiYtlGn5YgkuULntmaEeATZN2rCEXz2aKNenOGS1/qQlVZOgcHAgoe+lGKSx2hnSUazDwP/wnOHZ03Fl1lio5bMdgjNH33424amBFoZp0z9vP8zG29wrQZTz4nvNv/w8LYoyGCdMbwKBgQD7RctYSNYjwgM5Fb2V7R32CbHopatw6zfPEg2n/oYNjzxR6nT1N4jDtmswxLJy76jhNfSd00SeeaoglAHr9YW7959cxTULRAp6AhPB/JrvaEjmYDsMY+HHu9xS4cNlLYHDelt++G/IFoWcpaQkLBgz2bVUFZXZYG0bddt+6DhsxwKBgFD8Y9ipyM9U1aClRrGRkpU4WhRgYVWQl9KdFlJAXPS+kDABzoAW03AL5oP1SUSrO7BZEmkxNq8h8K8uTL/NKY9czo5n0w5E3EsDn5TexLimX/LhFWhoiX6y2/dfTHTM6H7F+vO9lQJ8Q616SjD0ffge8QZK6Lfo5Xl61392bYL3AoGBALLpl0hdVFxRoInc+QjvtQ2jCZJlpBTa5voCGMcta7Xr7z9EedLAoBj6oogvBgDgtJbnHC3kaE+OwZxAxr2ELJ0SS956bLKA2BTXtcnCW29sCbw/eMvxsqnNEavSJJa+OE8/qDZYHv+RScGoD5/R5cnFkl80n2YQSp/q40cRgluxAoGADBn2wRsjoE7vfirziBxb9WxL5ZQHvH1Nx3JSHKqtSsDuPQupM+lnKjbGYYI0kxa6Y0SB+7vyuax5u6pPvzcWYS2N9CCDGpCsS5B8lqKwI8mDvk9m6/g72RIq95WYJK2/fIQOv8jia2/ZnhUESNbzx9bpHGXirQGH28+uqTin/xw=";


    private Context mContext;
    private RelativeLayout rl_view, rl_av_load;
    private ImageView ej_iv_more, ej_iv_back;
    private TextView tv_share, tv_title, tv_debug;
    private AgentWebX5 mAgentWeb;

    private PopupWindow popupWindow;
    private Activity activity;
    private FragmentManager fragmentManager;
    private MiddleWareWebChromeBase mMiddleWareWebChrome;
    private MiddleWareWebClientBase mMiddleWareWebClient;

    private Disposable disposable;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Boolean addOrUpdate = false;
    private int ckcount = 0;


    private String roomData = "";
    //蓝牙
    private Boolean isLink = false, isPortOpen = false;
    private ACSUtility.blePort mCurrentPort, mSelectedPort;
    private ACSUtility util;
    private RxPermissions rxPermissions;

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

        rl_av_load = layout.findViewById(R.id.rl_av_load);
        tv_title = layout.findViewById(R.id.tv_title);
        ej_iv_back = layout.findViewById(R.id.ej_iv_back);
        tv_debug = layout.findViewById(R.id.tv_debug);
        backViewIsShow(false, "");
        ej_iv_more.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        ej_iv_back.setOnClickListener(this);
        tv_title.setOnClickListener(this);

        tv_share.setVisibility(View.GONE);
        //  ej_iv_more.setEnabled(false);


        EjuDrawEventCar.getDefault().post("app_qgz_measurement_Click");
        util = new ACSUtility(mContext, userCallback);
    }

    /**
     * @param activity        activity
     * @param fragmentManager fragmentManager
     * @param openUserId      用户唯一标示
     */
    public void initJddDrawRoomView(FragmentActivity activity, FragmentManager fragmentManager, String openUserId) {

        Config config = LogUtils.getConfig();
        config.setLogSwitch(BuildConfig.DEBUG);//false
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        initWeb(activity);
        rl_av_load.setVisibility(View.VISIBLE);
        EjuDrawBleEventCar.getDefault().register(this);


        Long date = TimeUtils.getNowMills();
        //加签
        JhomeSignature jhomeSignature = new JhomeSignature();


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", APP_Id);
        params.put("timestamp", date + "");
        params.put("openid", openUserId);
        params.put("company_id", COMPANY_ID);


        rxPermissions = new RxPermissions(activity);
        try {
            String sign = jhomeSignature.rsaSign(params, APPPRIVATE_KEY, JhomeConstants.CHARSET_UTF8);
            LogUtils.w("sign-------" + sign);
            DrawRoomInterface request = RetrofitManager.getDefault().provideClientApi(mContext);


            request.getOpenToken(ParameterUtils.prepareFormData(APP_Id), ParameterUtils.prepareFormData(openUserId), ParameterUtils.prepareFormData(COMPANY_ID), ParameterUtils.prepareFormData(date + ""), ParameterUtils.prepareFormData(sign))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResultDto<OpenYunDto>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResultDto<OpenYunDto> openYunDtoResultDto) {
                            rl_av_load.setVisibility(View.GONE);

                            if (openYunDtoResultDto.isOk() && null != openYunDtoResultDto.getData()) {
                                SharedPreferencesUtils.put(mContext, SharedPreferencesUtils.USER_TOKEN, openYunDtoResultDto.getData().getToken() + "");
                                SharedPreferencesUtils.put(mContext, SharedPreferencesUtils.USER_ID, openYunDtoResultDto.getData().getUser_id() + "");

                            } else {
                                ToastUtils.showShort(openYunDtoResultDto.getMsg());
                            }


                        }

                        @Override
                        public void onError(Throwable e) {
                            rl_av_load.setVisibility(View.GONE);
                            w("onError" + e.toString());
                        }

                        @Override
                        public void onComplete() {
                            rl_av_load.setVisibility(View.GONE);
                        }
                    });


        } catch (JhomeApiException e) {
            e.printStackTrace();
            rl_av_load.setVisibility(View.GONE);
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

        EjuDrawBleEventCar.getDefault().unregister(this);

        if (null != util) {
            util.closeACSUtility();
        }


    }

    private void initWeb(final Activity activity) {


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
                @SuppressLint("CheckResult")
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
                                    tv_share.setVisibility(View.GONE);
                                    ej_iv_more.setEnabled(true);
                                }
                            });
                            w("初始化完毕");

                            break;
                        case "save"://保存按钮
                            returnRoomData(roomDataDto.getData(), true);
                            break;

                        case "measure"://测距
                            if (!ButtonUtils.isFastDoubleClick()) {

                                if (isBluetoothEnabled()) {
                                    if (isLink) {
                                        setData();
                                    } else {
                                        Observable.just(1)
                                                .subscribeOn(AndroidSchedulers.mainThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<Integer>() {
                                                    @Override
                                                    public void accept(Integer integer) throws Exception {
                                                        rxPermissions
                                                                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                                                .subscribe(granted -> {
                                                                    if (granted) {
                                                                        LogUtils.w("有权限");
                                                                        Intent intent = new Intent(mContext, EnumPortActivity.class);
                                                                        mContext.startActivity(intent);
                                                                    } else {
                                                                        // Oups permission denied
                                                                        LogUtils.w("无权限");
                                                                        ToastUtils.showShort("请授予位置权限用来搜寻蓝牙设备");
                                                                    }
                                                                });

                                                    }
                                                });
                                    }
                                } else {
                                    //isLink = false;
                                    ToastUtils.showShort("请打开蓝牙");
                                }
                            }

                            break;
                        case "maidian":
                            EjuDrawEventCar.getDefault().post(roomDataDto.getData());
                            break;

                        case "reset":
                            w("重置");
                            addOrUpdate = false;
                            loadUrl("file:///android_asset/huxingmobile/index.html");

                            roomData = "";

                            break;

                        case "debug":
                            tv_debug.setText("日志：" + roomDataDto.getData());
                            break;


                    }


                }
            }));


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
     * @param data
     */
    private void openDrawRoom(String data) {
        final String myData = replaceBlank(data);
        mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_openHuxing", myData);


    }


    /**
     * 返回户型数据
     * 在需要处理业务逻辑的地方 实现EventBusTags.PUSH_ROOM_DATA 即可获得完成后的户型数据
     *
     * @param jsonString 户型数据 同以前一样
     * @param isH5Save   是否H5上面的保存按钮
     */
    @SuppressLint("CheckResult")
    public void returnRoomData(final String jsonString, Boolean isH5Save) {


        if (addOrUpdate) {

            Observable.just(1)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            saveRoomData(jsonString, "update", addOrUpdate);
                        }
                    });


        } else {
            Observable.just(1)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            CreateConstructionNameDalog createConstructionNameDalog = new CreateConstructionNameDalog(new DialogInterface() {
                                @Override
                                public void dialogCommit(String msg) {
                                    saveRoomData(jsonString, msg + "", addOrUpdate);
                                }

                                @Override
                                public void dialogFinish(String msg) {

                                }

                                @Override
                                public void dialogFinish() {

                                }
                            });

                            createConstructionNameDalog.show(fragmentManager, "createConstructionNameDalog");

                        }
                    });
        }

    }


    private void saveRoomData(String jsonString, final String constructionName, Boolean addOrUpdate) {


        rl_av_load.setVisibility(View.VISIBLE);
        final DrawRoomDataDto drawRoomDataDto = JsonUtils.fromJson(jsonString, DrawRoomDataDto.class);

        final DrawRoomInterface obRequest = RetrofitManager.getDefault().provideClientApi(mContext);


        //修改
        if (addOrUpdate) {

            obRequest.saveDrawingRoom(
                    ParameterUtils.prepareFormData(drawRoomDataDto.getNo()),
                    ParameterUtils.prepareFormData(drawRoomDataDto.getData()),
                    ParameterUtils.prepareFormData(drawRoomDataDto.getArea() + ""))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SaveRoomDto>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(SaveRoomDto saveRoomDto) {

                            if (null != saveRoomDto && "0".equals(saveRoomDto.getCode()) || "10000".equals(saveRoomDto.getCode())) {
                                roomData = drawRoomDataDto.getData();
                                ToastUtils.showShort("修改成功");
                            } else {
                                ToastUtils.showShort(saveRoomDto.getMsg());
                            }

                            rl_av_load.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showShort("修改户型失败，请稍后再试");
                            rl_av_load.setVisibility(View.GONE);
                        }

                        @Override
                        public void onComplete() {
                            rl_av_load.setVisibility(View.GONE);
                        }
                    });


        } else {
            //添加
            obRequest.saveDrawingRoom(
                    ParameterUtils.prepareFormData(drawRoomDataDto.getNo()),
                    ParameterUtils.prepareFormData(drawRoomDataDto.getData()),
                    ParameterUtils.prepareFormData(drawRoomDataDto.getArea() + "")
            ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<SaveRoomDto, ObservableSource<SaveRoomDto>>() {
                        @Override
                        public ObservableSource<SaveRoomDto> apply(SaveRoomDto saveRoomDto) throws Exception {
                            return obRequest.saveDrawingRoomProperty(ParameterUtils.prepareFormData(drawRoomDataDto.getNo()), ParameterUtils.prepareFormData(constructionName));
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SaveRoomDto>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            //  LogUtils.w("onSubscribe");
                            compositeDisposable.add(d);

                        }

                        @Override
                        public void onNext(SaveRoomDto saveRoomDto) {

                            w("onNext" + saveRoomDto.getCode() + "\n" + saveRoomDto.getMsg());

                            if (null != saveRoomDto && "0".equals(saveRoomDto.getCode()) || "10000".equals(saveRoomDto.getCode())) {
                                ToastUtils.showShort("保存成功");
                                roomData = drawRoomDataDto.getData();
                                setAddOrUpdate(true);
                            } else {
                                ToastUtils.showShort(saveRoomDto.getMsg());
                            }

                            rl_av_load.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showShort("保存失败，请稍后再试");
                            rl_av_load.setVisibility(View.GONE);
                        }

                        @Override
                        public void onComplete() {
                            //LogUtils.w("onComplete");
                            rl_av_load.setVisibility(View.GONE);
                        }
                    });
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ej_tv_share) {
            loadUrl("file:///android_asset/huxingmobile/index.html");
        } else if (v.getId() == R.id.ej_iv_more) {

            MorePopup morePopup = new MorePopup(this.activity, new MoreInterface() {
                @Override
                public void popDraw() {
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_showDefault()");
                    backViewIsShow(false, "");

                    EjuDrawEventCar.getDefault().post("app_qgz_measurement_Click");
                }

                @Override
                public void popFacade() {
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_showFacade()");
                    backViewIsShow(true, "立面图");
                    EjuDrawEventCar.getDefault().post("app_qgz_elevation_Click");
                }

                @Override
                public void pop3D() {

                    backViewIsShow(true, "3D模型");
                    EjuDrawEventCar.getDefault().post("app_qgz_abbr_Click");
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_show3d()");

                }

                @Override
                public void popHistory() {

                    isContent();


                }

                @Override
                public void popRepertoire() {
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_showAreaList()");
                    backViewIsShow(true, "面积清单");
                    EjuDrawEventCar.getDefault().post("app_qgz_area_Click");
                }
            });
            morePopup.setPopupGravity(Gravity.RIGHT);

            morePopup.showPopupWindow(v);


        } else if (v.getId() == R.id.ej_iv_back) {
            backViewIsShow(false, "");
            mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_showDefault()");
        } else if (v.getId() == R.id.tv_title) {
            ckcount++;
            if (ckcount == 5) {
                tv_debug.setVisibility(View.VISIBLE);
                ckcount = 0;
            } else {
                tv_debug.setVisibility(View.GONE);
            }

        }
    }


    private void backViewIsShow(Boolean isShow, String title) {

        if (isShow) {
            EjuDrawEventCar.getDefault().post(new IsShowBarDto(false));
            ej_iv_more.setVisibility(View.GONE);
            ej_iv_back.setVisibility(View.VISIBLE);
            setMargins(tv_title, dpToPx(mContext, 0), 0, 0, 0);
            tv_title.setText(title);
            tv_share.setVisibility(View.GONE);
        } else {
            EjuDrawEventCar.getDefault().post(new IsShowBarDto(true));
            ej_iv_more.setVisibility(View.VISIBLE);
            ej_iv_back.setVisibility(View.GONE);
            setMargins(tv_title, dpToPx(mContext, 21), 0, 0, 0);
            tv_title.setText("智能量房");
            tv_share.setVisibility(View.GONE);
        }

    }


    private int dpToPx(Context context, float dpValue) {//dp转换为px
        float scale = context.getResources().getDisplayMetrics().density;//获得当前屏幕密度
        return (int) (dpValue * scale + 0.5f);
    }


    private void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    //接收蓝牙设备
    @Override
    public void update(Object obj) {


        if ((obj instanceof OpenRoomDto)) {


            w("打开户型");
            OpenRoomDto openRoomDto = (OpenRoomDto) obj;
            addOrUpdate = openRoomDto.getAddOrUpdata();
            openDrawRoom(openRoomDto.getData() + "");
            roomData = "";
            return;
        } else {
            w("蓝牙");
            if (null != obj) {
                BluetoothDevice device = (BluetoothDevice) obj;
                //链接
                w("接收到的蓝牙设备名字\n" + device.getName());

                mSelectedPort = util.new blePort(device);


                try {
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mSelectedPort != null) {
                                w("我有值-------去链接");

                                if (isPortOpen) {
                                    w("isPortOpen-------");
                                    util.closePort();
                                } else if (mSelectedPort != null) {
                                    w("mSelectedPort--------");
                                    // 等待窗口
                                    util.openPort(mSelectedPort);
                                    rl_av_load.setVisibility(View.VISIBLE);
                                } else {
                                    ToastUtils.showShort("没有选择设备");
                                }

                            }
                        }
                    }, 500L);
                } catch (Exception e) {
                    rl_av_load.setVisibility(View.GONE);
                    //ToastUtils.showShort("请重新选择设备");
                }
            }
        }

    }

    /**
     * 画布上是否存在数据
     *
     * @return
     */
    private boolean isContent() {

        mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_isEmpty", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {


                String mValue = value.replaceAll("\"", "").toString();
                if ("true".equals(mValue)) {
                    LogUtils.w("是没有数据");
                    Intent intent = new Intent(activity, MyRoomDataList.class);
                    activity.startActivity(intent);
                    EjuDrawEventCar.getDefault().post("app_qgz_record_Click");

                } else {
                    //判断下数据是否保存过没，没有保存过出弹出提示
                    LogUtils.w("是有数据");


                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_hasChanged", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            String mValue = value.replaceAll("\"", "").toString();
                            if ("true".equals(mValue)) {

                                DeleteRoomDalog saveDalog = new DeleteRoomDalog(new DialogInterface() {
                                    @Override
                                    public void dialogCommit(String msg) {

                                        mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_save");
                                    }

                                    @Override
                                    public void dialogFinish(String msg) {

                                    }

                                    @Override
                                    public void dialogFinish() {
                                        Intent intent = new Intent(activity, MyRoomDataList.class);
                                        activity.startActivity(intent);
                                        EjuDrawEventCar.getDefault().post("app_qgz_record_Click");
                                    }
                                }, "提示", "是否保存所绘图形", "确定", "取消");


                                saveDalog.show(fragmentManager, "saveDalog");


                            } else {

                                Intent intent = new Intent(activity, MyRoomDataList.class);
                                activity.startActivity(intent);
                                EjuDrawEventCar.getDefault().post("app_qgz_record_Click");
                            }

                        }

                    });
//
//                    if (roomData.length() < 10) {
//
//                    } else {
//
//
//                    }


                }

            }
        });
        return false;
    }

    private ACSUtility.IACSUtilityCallback userCallback = new ACSUtility.IACSUtilityCallback() {

        @Override
        public void didFoundPort(ACSUtility.blePort newPort) {
            // TODO Auto-generated method stub
            w("didFoundPort-----");
        }

        @Override
        public void didFinishedEnumPorts() {
            // TODO Auto-generated method stub
            w("didFinishedEnumPorts---");
        }

        @Override
        public void didOpenPort(ACSUtility.blePort port, Boolean bSuccess) {
            w("didOpenPort---");
            // TODO Auto-generated method stub
            d("The port is open ? " + bSuccess);
            if (bSuccess) {
                isPortOpen = true;
                mCurrentPort = port;
                isLink = true;

                ToastUtils.showShort("蓝牙链接成功");

                rl_av_load.setVisibility(View.GONE);

            } else {
                isLink = true;
                ToastUtils.showShort("蓝牙链接失败");
                rl_av_load.setVisibility(View.GONE);
            }

        }


        @Override
        public void didClosePort(ACSUtility.blePort port) {
            w("didClosePort---");
            rl_av_load.setVisibility(View.GONE);
            isPortOpen = false;
            mCurrentPort = null;
            //断开重置状态
            isLink = false;

        }

        @Override
        public void didPackageReceived(ACSUtility.blePort port, byte[] packageToSend) {
            w("didPackageReceived---");
            // TODO Auto-generated method stub


            if (packageToSend != null) {
                int loopi;
                double sum = 0;
                for (loopi = 3; loopi < 7; loopi++) {
                    sum = packageToSend[loopi] + sum * 256;
                }

                if (sum > 0) {
                    double size = sum / 10000;
                    //LogUtils.w("计算结果是" + size);
                    if ((int) size < 80000) {
                        w("还进来》？");
                        mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_measure", size + "");
                    }
                }
            }

        }

        @Override
        public void heartbeatDebug() {
            w("heartbeatDebug----------");
            // TODO Auto-generated method stub

        }

        @Override
        public void utilReadyForUse() {
            w("utilReadyForUse---------");
            // TODO Auto-generated method stub

        }

        @Override
        public void didPackageSended(boolean succeed) {
            w("didPackageSended--------" + succeed);
        }

    };

    private void setData() {
        String data = "ATK001#";
        ByteArrayOutputStream bab = new ByteArrayOutputStream(data.length() / 2);
        byte[] dataBytes = data.getBytes();
        bab.write(dataBytes, 0, dataBytes.length);
        util.writePort(bab.toByteArray());
    }


    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }

        return false;
    }

    private void setAddOrUpdate(Boolean isadd) {
        this.addOrUpdate = isadd;
    }

}
