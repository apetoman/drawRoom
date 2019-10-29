package com.eju.cy.drawlibrary.activity

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.eju.cy.drawlibrary.R
import com.eju.cy.drawlibrary.adapter.BluetoothDeviceAdapter
import com.eju.cy.drawlibrary.bluetooth.ACSUtility
import com.eju.cy.drawlibrary.plug.EjuDrawBleEventCar
import kotlinx.android.synthetic.main.activity_enum_port.*


/**
 * @ Name: Caochen
 * @ Date: 2019-08-16
 * @ Time: 10:21
 * @ Description：  搜索蓝牙设备
 */
class EnumPortActivity : AppCompatActivity() {

    var deviceList = arrayListOf<BluetoothDevice>()
    private var utility: ACSUtility? = null
    private var mNewtPort: ACSUtility.blePort? = null
    private var utilAvaliable: Boolean = false

    lateinit var adapter: BluetoothDeviceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_enum_port)
        initView()

    }

    private fun initView() {

        rl_list.layoutManager = LinearLayoutManager(this)
        this.adapter = BluetoothDeviceAdapter(R.layout.item_show_bluetooth_list, deviceList)
        rl_list.adapter = adapter

        iv_close.setOnClickListener {

            this.finish()
        }

        adapter.setOnItemClickListener { adapter, view, position ->


            val device = deviceList!!.get(position)
            utility!!.stopEnum()
            EjuDrawBleEventCar.getDefault().post(device)
            finish()


//
//            val b = Bundle()
//            b.putParcelable(BluetoothDevice.EXTRA_DEVICE, device)
//            val result = Intent()
//            result.putExtras(b)
//            setResult(Activity.RESULT_OK, result)
//            finish()

        }

        utility = ACSUtility(this, userCallback)

    }


    var userCallback = object : ACSUtility.IACSUtilityCallback {
        override fun utilReadyForUse() {

            utilAvaliable = true
            utility!!.enumAllPorts(20f)
        }

        override fun didFoundPort(newPort: ACSUtility.blePort?) {

            mNewtPort = newPort


            runOnUiThread {
                addDevice(mNewtPort!!._device);
            }
        }

        override fun didFinishedEnumPorts() {
            if (deviceList!!.size == 0) {
                ToastUtils.showShort(" 附近没有可用蓝牙设备，请重试")
                finish()
            }
        }

        override fun didOpenPort(port: ACSUtility.blePort?, bSuccess: Boolean?) {
        }

        override fun didClosePort(port: ACSUtility.blePort?) {
        }

        override fun didPackageSended(succeed: Boolean) {
        }

        override fun didPackageReceived(port: ACSUtility.blePort?, packageToSend: ByteArray?) {
        }

        override fun heartbeatDebug() {
        }


    }


    /**
     * 添加蓝牙设备
     */
    private fun addDevice(device: BluetoothDevice) {

        var deviceFound = false

        for (listDev in deviceList) {
            if (listDev.address == device.address) {
                //之前的扫描已经扫描过了这个设备，把标志设为真，就不需要再添加在deviceList中了
                deviceFound = true
                break
            }
        }

//        device.name.let {
//            LogUtils.w("设备"+device.name)
//
//        }


        if (!deviceFound) {
            if (null != device.name && device.name.replace(" ", "") == "Myhome3D") {
                deviceList.add(device)
                adapter.notifyDataSetChanged()
            }


        }


    }


    override fun onDestroy() {
        super.onDestroy()

        if (null != utility) {
            //utility.setUserCallback(null);
            if (utilAvaliable) {
                utility!!.stopEnum()
            }
            utility!!.closeACSUtility()
        }
    }


}
