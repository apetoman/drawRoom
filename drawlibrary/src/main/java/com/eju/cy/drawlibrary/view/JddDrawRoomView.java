package com.eju.cy.drawlibrary.view;

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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.arhuxinglibrary.bean.RoomDataDto;
import com.eju.cy.drawlibrary.R;
import com.eju.cy.drawlibrary.activity.EnumPortActivity;
import com.eju.cy.drawlibrary.activity.MyRoomDataList;
import com.eju.cy.drawlibrary.bean.DrawRoomDataDto;
import com.eju.cy.drawlibrary.bean.OpenRoomDto;
import com.eju.cy.drawlibrary.bean.SaveRoomDto;
import com.eju.cy.drawlibrary.bluetooth.ACSUtility;
import com.eju.cy.drawlibrary.dialog.CreateConstructionNameDalog;
import com.eju.cy.drawlibrary.net.DrawRoomInterface;
import com.eju.cy.drawlibrary.plug.DialogInterface;
import com.eju.cy.drawlibrary.plug.EjuDrawBleEventCar;
import com.eju.cy.drawlibrary.plug.EjuDrawEventCar;
import com.eju.cy.drawlibrary.plug.EjuDrawObserver;
import com.eju.cy.drawlibrary.pop.MoreInterface;
import com.eju.cy.drawlibrary.pop.MorePopup;
import com.eju.cy.drawlibrary.utils.ButtonUtils;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @ Name: Caochen
 * @ Date: 2019-08-19
 * @ Time: 10:54
 * @ Description： 户型绘制View
 */
public class JddDrawRoomView extends RelativeLayout implements View.OnClickListener, EjuDrawObserver {
    private Context mContext;
    private RelativeLayout rl_view, rl_av_load;
    private ImageView ej_iv_more, ej_iv_back;
    private TextView tv_share, tv_title;
    private AgentWebX5 mAgentWeb;

    private PopupWindow popupWindow;
    private Activity activity;
    private FragmentManager fragmentManager;
    private MiddleWareWebChromeBase mMiddleWareWebChrome;
    private MiddleWareWebClientBase mMiddleWareWebClient;

    private Disposable disposable;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Boolean addOrUpdate = false;


    //蓝牙
    private Boolean isLink = false, isPortOpen = false;
    private ACSUtility.blePort mCurrentPort, mSelectedPort;
    private ACSUtility util;


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
        backViewIsShow(false, "");
        ej_iv_more.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        ej_iv_back.setOnClickListener(this);

        tv_share.setVisibility(View.GONE);
        ej_iv_more.setEnabled(false);


        EjuDrawEventCar.getDefault().post("app_qgz_measurement_Click");
        util = new ACSUtility(mContext, userCallback);
    }


    public void initJddDrawRoomView(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        initWeb(activity);

        EjuDrawBleEventCar.getDefault().register(this);


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
                        case "save"://保存按钮
                            returnRoomData(roomDataDto.getData(), true);
                            break;

                        case "measure"://测距
                            if (!ButtonUtils.isFastDoubleClick()) {

                                if (isBluetoothEnabled()) {
                                    if (isLink) {
                                        setData();
                                    } else {
                                        Intent intent = new Intent(mContext, EnumPortActivity.class);
                                        mContext.startActivity(intent);
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

        //修改
        if (addOrUpdate) {

            obRequest.saveDrawingRoom(headersMap,
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
                                ToastUtils.showShort("保存成功");
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
                            return obRequest.saveDrawingRoomProperty(headersMap, ParameterUtils.prepareFormData(drawRoomDataDto.getNo()), ParameterUtils.prepareFormData(constructionName));
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

                            LogUtils.w("onNext" + saveRoomDto.getCode());

                            if (null != saveRoomDto && "0".equals(saveRoomDto.getCode()) || "10000".equals(saveRoomDto.getCode())) {
                                ToastUtils.showShort("保存成功");
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
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_show3d()");
                    backViewIsShow(true, "3D模型");
                    EjuDrawEventCar.getDefault().post("app_qgz_abbr_Click");
                }

                @Override
                public void popHistory() {
                    Intent intent = new Intent(activity, MyRoomDataList.class);
                    activity.startActivity(intent);
                    EjuDrawEventCar.getDefault().post("app_qgz_record_Click");
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
        }
    }


    private void backViewIsShow(Boolean isShow, String title) {

        if (isShow) {
            ej_iv_back.setVisibility(View.VISIBLE);
            setMargins(tv_title, dpToPx(mContext, 0), 0, 0, 0);
            tv_title.setText(title);
            tv_share.setVisibility(View.GONE);
        } else {
            ej_iv_back.setVisibility(View.GONE);
            setMargins(tv_title, dpToPx(mContext, 21), 0, 0, 0);
            tv_title.setText("智能量房");
            tv_share.setVisibility(View.VISIBLE);
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


            LogUtils.w("打开户型");
            OpenRoomDto openRoomDto = (OpenRoomDto) obj;
            addOrUpdate = openRoomDto.getAddOrUpdata();
            openDrawRoom(openRoomDto.getData() + "");
            return;
        } else {
            LogUtils.w("蓝牙");
            if (null != obj) {
                BluetoothDevice device = (BluetoothDevice) obj;
                //链接
                LogUtils.w("接收到的蓝牙设备名字\n" + device.getName());

                mSelectedPort = util.new blePort(device);


                try {
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mSelectedPort != null) {
                                LogUtils.w("我有值-------去链接");

                                if (isPortOpen) {
                                    LogUtils.w("isPortOpen-------");
                                    util.closePort();
                                } else if (mSelectedPort != null) {
                                    LogUtils.w("mSelectedPort--------");
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

    private ACSUtility.IACSUtilityCallback userCallback = new ACSUtility.IACSUtilityCallback() {

        @Override
        public void didFoundPort(ACSUtility.blePort newPort) {
            // TODO Auto-generated method stub
            LogUtils.w("didFoundPort-----");
        }

        @Override
        public void didFinishedEnumPorts() {
            // TODO Auto-generated method stub
            LogUtils.w("didFinishedEnumPorts---");
        }

        @Override
        public void didOpenPort(ACSUtility.blePort port, Boolean bSuccess) {
            LogUtils.w("didOpenPort---");
            // TODO Auto-generated method stub
            LogUtils.d("The port is open ? " + bSuccess);
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
            LogUtils.w("didClosePort---");
            rl_av_load.setVisibility(View.GONE);
            isPortOpen = false;
            mCurrentPort = null;
            //断开重置状态
            isLink = false;

        }

        @Override
        public void didPackageReceived(ACSUtility.blePort port, byte[] packageToSend) {
            LogUtils.w("didPackageReceived---");
            // TODO Auto-generated method stub


            if (packageToSend != null) {
                int loopi;
                double sum = 0;
                for (loopi = 3; loopi < 7; loopi++) {
                    sum = packageToSend[loopi] + sum * 256;
                }

                if (sum > 0) {
                    double size = sum / 10000;
                    LogUtils.w("计算结果是" + size);
                    mAgentWeb.getJsEntraceAccess().quickCallJs("CallJS.app_js_measure", size + "");
                }
            }

        }

        @Override
        public void heartbeatDebug() {
            LogUtils.w("heartbeatDebug----------");
            // TODO Auto-generated method stub

        }

        @Override
        public void utilReadyForUse() {
            LogUtils.w("utilReadyForUse---------");
            // TODO Auto-generated method stub

        }

        @Override
        public void didPackageSended(boolean succeed) {
            LogUtils.w("didPackageSended--------" + succeed);
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

}
