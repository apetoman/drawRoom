package com.eju.cy.drawlibrary.adapter

import android.text.format.DateUtils
import android.widget.ImageView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eju.cy.drawlibrary.R
import com.eju.cy.drawlibrary.bean.MyRoomData
import java.text.SimpleDateFormat
import java.util.*


class MyRoomDataAdapter(layoutResId: Int, data: MutableList<MyRoomData.DataBean.RecordsBean>?) :
    BaseQuickAdapter<MyRoomData.DataBean.RecordsBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder?, item: MyRoomData.DataBean.RecordsBean?) {


        if (null == item!!.name || "" == item!!.name || "null" == item!!.name) {
            helper!!.setText(R.id.tv_community, "未知工地")
        } else {
            helper!!.setText(R.id.tv_community, item!!.name)
        }

        if (null != item!!.modify_time) {
            var date =
                TimeUtils.millis2String(item!!.modify_time * 1000, SimpleDateFormat("yyyy/MM/dd"))
            helper!!.setText(R.id.tv_time, date + "")
        } else {
            helper!!.setText(R.id.tv_time, "")
        }


        var img = helper.getView<ImageView>(R.id.iv_room_img)
        Glide.with(img).load(item.preview_url).into(img)
    }


}