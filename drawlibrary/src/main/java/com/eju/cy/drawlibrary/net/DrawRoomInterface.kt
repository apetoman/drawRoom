package com.eju.cy.drawlibrary.net

import com.eju.cy.drawlibrary.bean.MyRoomData
import com.eju.cy.drawlibrary.bean.ResultDto
import com.eju.cy.drawlibrary.bean.SaveDrawingRoomDto
import com.eju.cy.drawlibrary.bean.SaveRoomDto
import io.reactivex.Observable
import okhttp3.Call
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.Multipart


interface DrawRoomInterface {


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
    fun saveDrawingRoom(
        @HeaderMap headers: Map<String, String>,
        @Part("no") no: RequestBody,
        @Part("data") data: RequestBody,
        @Part("area") area: RequestBody

    ): Observable<SaveRoomDto>


    @GET("/deco/huxing/own/")
    fun getMyRoomList(
        @HeaderMap headers: Map<String, String>,
        @Query("start_index") start_index: String,
        @Query("count") count: String
    ): Observable<MyRoomData>


    /**
     * 客户端提交户属性保存api
     *
     * @param no             户型编号
     * @param name           户型名称
     * @param layout         房型
     * @param city_id        所在城市id
     * @param community_id   小区id
     * @param community_name 小区名
     * @return
     */
    @Multipart
    @POST("/deco/huxing/save_property/")
    fun saveDrawingRoomProperty(
        @HeaderMap headers: Map<String, String>,
        @Part("no") no: RequestBody,
        @Part("name") name: RequestBody
    ): Observable<SaveRoomDto>


}