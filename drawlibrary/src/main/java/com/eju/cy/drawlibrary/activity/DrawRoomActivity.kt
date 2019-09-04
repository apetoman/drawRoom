package com.eju.cy.drawlibrary.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.eju.cy.drawlibrary.R
import com.eju.cy.drawlibrary.bluetooth.ACSUtility
import kotlinx.android.synthetic.main.activity_draw_room.*
import com.blankj.utilcode.util.LogUtils
import java.io.ByteArrayOutputStream
import android.R.attr.data
import androidx.core.app.NotificationCompat.getExtras
import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.view.View


class DrawRoomActivity : AppCompatActivity() {
    private var isLink: Boolean = false//是否第一次


    private var isPortOpen = false
    var mCurrentPort: ACSUtility.blePort? = null
    lateinit var mSelectedPort: ACSUtility.blePort
    lateinit var util: ACSUtility


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_room)


        initView()
    }

    private fun initView() {
        util = ACSUtility(this, userCallback)

        tv_select.setOnClickListener {

            if (isBluetoothEnabled()) {
                if (!isLink) {

                    var intent = Intent(this, EnumPortActivity::class.java)
                    startActivityForResult(intent, 23)
                } else {
                    //链接
                    setData()
                }
            } else {
                ToastUtils.showShort("请打开蓝牙")
            }


        }
    }


    fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter()

        return bluetoothAdapter?.isEnabled ?: false

    }

    //测量命令
    fun setData() {

        val data = "ATK001#"
        val bab = ByteArrayOutputStream(data.length / 2)
        val dataBytes = data.toByteArray()
        bab.write(dataBytes, 0, dataBytes.size)
        util.writePort(bab.toByteArray())
        rl_av_load.visibility = View.VISIBLE


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == 23 && resultCode == Activity.RESULT_OK) {


            val bundle = data!!.extras
            val device: BluetoothDevice = bundle.getParcelable(BluetoothDevice.EXTRA_DEVICE)

            LogUtils.w("BluetoothDevice----"+device.name)
            mSelectedPort = util.blePort(device)


            Handler().postDelayed({
                if (mSelectedPort != null){

                    if (isPortOpen) {
                        LogUtils.w("isPortOpen-------");
                        util.closePort();
                    } else if (mSelectedPort != null) {
                        LogUtils.w("mSelectedPort--------");
                        // 等待窗口
                        util.openPort(mSelectedPort);

                        rl_av_load.visibility = View.VISIBLE

                    }



                }
            }, 2000)


        }

    }


    //蓝牙

    var userCallback = object : ACSUtility.IACSUtilityCallback {
        override fun utilReadyForUse() {

            LogUtils.w("utilReadyForUse")
        }

        override fun didFoundPort(newPort: ACSUtility.blePort?) {

            LogUtils.w("didFoundPort")

        }

        override fun didFinishedEnumPorts() {
            LogUtils.w("didFinishedEnumPorts")

        }

        override fun didOpenPort(port: ACSUtility.blePort?, bSuccess: Boolean?) {

            if (bSuccess!!) {
                isPortOpen = true
                mCurrentPort = port!!

                ToastUtils.showShort("蓝牙连接成功")
                rl_av_load.visibility = View.GONE
                isLink = true

            } else {
                ToastUtils.showShort("蓝牙连接失败")
                rl_av_load.visibility = View.GONE
                isLink = false
            }

        }

        override fun didClosePort(port: ACSUtility.blePort?) {
            isPortOpen = false
            mCurrentPort = null
            rl_av_load.visibility = View.GONE
            //断开重置状态
            isLink = false


        }

        override fun didPackageSended(succeed: Boolean) {
            LogUtils.w("didPackageSended")
            rl_av_load.visibility = View.GONE
        }

        override fun didPackageReceived(port: ACSUtility.blePort?, packageToSend: ByteArray?) {

            if (packageToSend != null) {
                var loopi: Int
                var sum = 0.0
                loopi = 3
                while (loopi < 7) {
                    sum = packageToSend[loopi] + sum * 256
                    loopi++
                }

                if (sum > 0) {
                    val size = sum / 10000
                    LogUtils.w("计算结果是$size")

                }

                rl_av_load.visibility = View.GONE
            }


        }

        override fun heartbeatDebug() {
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        util.closeACSUtility();

    }


}
