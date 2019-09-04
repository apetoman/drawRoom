package com.eju.cy.drawlibrary.net;


import com.eju.cy.drawlibrary.bean.ResultDto;
import com.eju.cy.drawlibrary.bean.SaveDrawingRoomDto;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @author-eju
 * @time-2018/6/14 上午9:48
 * @function-同步接口
 */

public interface SynchronousService {


    /**
     * 客户端户型数据保存API
     *
     * @param no      户型编号
     * @param data    户型数据
     * @param area    户型面积
     * @param photo2d 户型平面图
     * @return
     */
    @Multipart
    @POST("/deco/huxing/save/")
    Observable<String> saveDrawingRoom(
            @HeaderMap Map<String, String> headers,
            @Part("no") RequestBody no,
            @Part("data") RequestBody data,
            @Part("area") RequestBody area,
            @Part("photo2d") RequestBody photo2d);







}
