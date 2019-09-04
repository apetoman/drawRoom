package com.eju.cy.drawlibrary.net;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.drawlibrary.BuildConfig;
import com.eju.cy.drawlibrary.utils.SharedPreferencesUtils;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    public static final String SERVER_URL = "https://yun.jiandanhome.com/";
    private static RetrofitManager instance;

    public static RetrofitManager getDefault() {


        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }


   public DrawRoomInterface provideClientApi(final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(genericClient(context))
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();
        return retrofit.create(DrawRoomInterface.class);

    }

  private OkHttpClient genericClient(final Context context) {





        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        String userId = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.USER_ID, "");
                        String token = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.USER_TOKEN, "");

                        LogUtils.w("userId" + userId + "\n" + token);
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("User-Id", userId)
                                .addHeader("User-Token", token)
                                .addHeader("X-REQUESTED-WITH", "json")
                                .build();
                        return chain.proceed(request);
                    }

                })
                .addInterceptor(  new LoggingInterceptor.Builder()
                        .loggable(BuildConfig.DEBUG)
                        .setLevel(Level.BASIC)
                        .log(Platform.INFO)
                        .request("Request")
                        .response("Response")
                        .build())
                .build();

        return httpClient;
    }


}

