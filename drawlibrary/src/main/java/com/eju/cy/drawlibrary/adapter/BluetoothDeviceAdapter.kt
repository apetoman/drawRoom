package com.eju.cy.drawlibrary.adapter

import android.bluetooth.BluetoothDevice
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eju.cy.drawlibrary.R

class BluetoothDeviceAdapter(layoutResId: Int, data: MutableList<BluetoothDevice>?) :
    BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder?, item: BluetoothDevice?) {


        helper!!.setText(R.id.tv_id, item!!.address)

        if (null == item!!.name || "null" == item!!.name || "" == item!!.name) {
            helper!!.setText(R.id.tv_name, "未知设备")
        } else {
            helper!!.setText(R.id.tv_name, item?.name)
        }


    }
}