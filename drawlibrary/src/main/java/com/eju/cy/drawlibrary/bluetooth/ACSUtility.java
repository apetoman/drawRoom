package com.eju.cy.drawlibrary.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;


public class ACSUtility extends Object {
	
	
	private Context context;
	private Thread timerThread;
	
	private ArrayList<blePort> ports = null;
	private blePort	currentPort = null;
	private int _lengthOfPackage = 10;
	private float _scanTime;
	private Boolean bScanning;
	
	private byte[] receivedBuffer;
	
	private boolean isInitializing = true;
	
	
	
	private  final String tag = "ACSUtility";
	private  final int ACSUTILITY_SCAN_TIMEOUT_MSG = 0x01;
	
	
	
	private BluetoothAdapter mBtAdapter;
	//private  BluetoothGattAdapter 	mBtGattAdapter;
	//private  BluetoothGatt 	   		mBtGatt;
	
	private BluetoothDevice mDevice;
	
	private ACSUtilityService		mService;
	
	private BluetoothManager bluetoothManager = null;
	
	private  IACSUtilityCallback userCallback;


	public ACSUtility(){
		Log.d(tag, "ACS Utility Constructor");
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	public ACSUtility(Context context, IACSUtilityCallback cb) {
		// TODO Auto-generated constructor stub
	   //构造函数，初始化所有变量
		this.context = context;
		userCallback = cb;
		_lengthOfPackage = 10;
		bScanning = false;

		Log.d(tag, "acsUtility 1");

		bluetoothManager =
		        (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBtAdapter = bluetoothManager.getAdapter();
		if (mBtAdapter == null) {
			Log.d(tag, "error,mBtAdapter == null");
			return;
		}

		//context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		Intent intent = new Intent();
		intent.setClass(context, ACSUtilityService.class);
		context.startService(intent);
		context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection conn = new ServiceConnection() {

		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			//mService = ((ACSBinder)service).getServie();
			Log.d(tag, "ACSUtilityService is connected!");
			mService = ((ACSUtilityService.ACSBinder)service).getService();
			mService.initialize();
			mService.addEventHandler(eventHandler);
			//
			//ready to use
			userCallback.utilReadyForUse();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.d(tag, "ACSUtilityService is disConnected!");
			mService = null;
		}

	};
	boolean mIsPortOpen = false;
	@SuppressLint("HandlerLeak")
	private Handler eventHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//super.handleMessage(msg);
			Log.e(tag, "EventHandler got a message_from.flag is " + msg.what);
			if (userCallback == null) {
				Log.e(tag, "UserCallback is null! All event will not be handled!");
				return;
			}
			switch (msg.what) {
			case ACSUtilityService.EVENT_GATT_CONNECTED:

				break;
			case ACSUtilityService.EVENT_GATT_DISCONNECTED:
				userCallback.didClosePort(currentPort);
				mIsPortOpen = false;
				break;
			case ACSUtilityService.EVENT_GATT_SERVICES_DISCOVERED:

				break;

			case ACSUtilityService.EVENT_OPEN_PORT_SUCCEED:

				userCallback.didOpenPort(currentPort, true);
				mIsPortOpen = true;
				break;
			case ACSUtilityService.EVENT_OPEN_PORT_FAILED:

				userCallback.didOpenPort(currentPort, false);
				mIsPortOpen = true;
				break;
			case ACSUtilityService.EVENT_DATA_AVAILABLE:
				Bundle data = msg.getData();
				byte[] receivedData = data.getByteArray(ACSUtilityService.EXTRA_DATA);
				userCallback.didPackageReceived(currentPort, receivedData);
				break;

			case ACSUtilityService.EVENT_HEART_BEAT_DEBUG:

				userCallback.heartbeatDebug();
				break;

			case ACSUtilityService.EVENT_DATA_SEND_SUCEED:
				// 数据发送成功
				userCallback.didPackageSended(true);
				break;
			case ACSUtilityService.EVENT_DATA_SEND_FAILED:
				// 数据发送失败
				userCallback.didPackageSended(false);
				break;
			default:
				break;
			}
		}

	};

	// 串口枚举
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void enumAllPorts(float time){
			//UUID toFoundUUIDs[] = {ACS_SERVICE_UUID};
			ports = null;//清空缓冲区
			_scanTime = time;
			if (bScanning) {
				Log.e(tag, "enum in progress,could not execute again");
				return;
			}
			Log.d(tag, "start scan now");
			mBtAdapter.stopLeScan(mLeScanCallback);
			mBtAdapter.startLeScan(mLeScanCallback);
			//UUID []serviceUuids = {ACSUtilityService.ACS_SERVICE_UUID};
			//mBtAdapter.startLeScan(serviceUuids, mLeScanCallback);
			bScanning = true;
			//定时启动
			timerThread	 = new Thread(new myThread());
			timerThread.start();

	}

	/*public void setUserCallback(IACSUtilityCallback userCallback) {
		this.userCallback = userCallback;
	}*/
	public boolean isPortOpen(blePort port) {

		/*if (mService != null && mService.mBluetoothGatt != null) {
			if (bluetoothManager.getConnectionState(port._device, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED) {

				return true;
			}
		}
		Log.e(tag, "mService or mService.mBluetoothGatt is null.We can't know whether the port is opened.");
		return false;*/
		return (mIsPortOpen && port._device.equals(currentPort._device)) ?
				true : false;

	}
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			Log.d(tag, "onScanResult() - deviceName = " + device.getName()
					+ ", rssi=" + rssi + ",lengthOfScanRecord is : "
					+ scanRecord.length + ",address : " + device.getAddress());

			if (/* device.getName().equals("BDE_ACS_Module") */true) {
				// Log.d(tag, "onScanResult() - device=" + device + ", rssi=" +
				// rssi + ",lengthOfScanRecord is : "+ scanRecord.length);
				// Log.d(tag, "found ACS Module");
				if (checkAddressExist(device)) {
					// 同样设备的多个包
					// Log.d(tag, "found same ACS Module");
				} else {
					if (ports == null) {
						ports = new ArrayList<blePort>();
					}

					// 添加新端口
					Log.d(tag, "==== new Port add here ====");
					blePort newPort = new blePort(device);
					ports.add(newPort);

					if (device.getName()!=null && device.getName().equals("BDE_SPP")) {
						// 断点
						int i = 0;
						i++;
					}

					if (userCallback != null) {
						userCallback.didFoundPort(newPort);
					}

					/*// 包解析

					int flag;
					int recordStart = 0;
					int length = scanRecord[recordStart++];
					while (length > 0) {
						flag = scanRecord[recordStart++];
						if (flag == 2 && scanRecord[recordStart++] == -80
								&& scanRecord[recordStart++] == -1) {
							if (userCallback != null) {
								userCallback.didFoundPort(newPort);
							}
							break;
						}
						recordStart += (length - 1);
						length = scanRecord[recordStart++];
					}*/

				}
			}
		}
	};

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void stopEnum() {
		bScanning = false;
		mBtAdapter.stopLeScan(mLeScanCallback);
	}

	//串口打开
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void openPort(blePort port){

		if (mService != null && port != null) {
			currentPort = port;
			mService.connect(port._device.getAddress());
		}
		else {
			Log.e(tag, "ACSUtilityService or port is null!");
		}

	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void closePort(){
		/*if (mBtGatt == null) {
			return;
		}*/

		mService.disconnect();

	}

	public void configurePort(blePort port,int lenghOfPackage){
		_lengthOfPackage = lenghOfPackage;
	}

	public boolean writePort(byte[] value){
		if (value != null && mIsPortOpen) {
			return mService.writePort(value);
		}
		Log.e(tag, "Write port failed...value is null...");

		return false;
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void closeACSUtility() {
		//BluetoothGattAdapter.closeProfileProxy(BluetoothGattAdapter.GATT, mBtGatt);
		mService.close();
		//closePort();
		mService.removeEventHandler();
		context.unbindService(conn);
		Intent intent = new Intent();
		intent.setClass(context, ACSUtilityService.class);
		context.stopService(intent);
	}



	//utility
	private void openPortFailAction(){
		if (userCallback != null) {
			userCallback.didOpenPort(currentPort, false);
		}
	}
	private void openPortSuccessAction(){
		if (userCallback != null) {
			userCallback.didOpenPort(currentPort, true);
		}
	}

	private String bytesToHexString(byte[] src){
	        StringBuilder stringBuilder = new StringBuilder("");
	        if (src == null || src.length <= 0) {
	            return null;
	        }
	        for (int i = 0; i < src.length; i++) {
	            int v = src[i] & 0xFF;
	            String hv = Integer.toHexString(v);
	            if (hv.length() < 2) {
	                stringBuilder.append(0);
	            }
	            stringBuilder.append(hv);
	        }
	        stringBuilder.append('\n');
	        return stringBuilder.toString();
	    }

	//回调
	private Boolean checkAddressExist(BluetoothDevice device){
		 if (ports == null) {
			return false;
		}
		 for (blePort port : ports) {
			if (port._device.getAddress().equals(device.getAddress())) {
				return true;
			}
		}

		 return false;
	 }
	private void checkPackageToSend(byte[] newData){
	if (receivedBuffer != null) {
		// 上次接收了的但是没有处理的数据不为null，与这次接收的一块处理
		Log.d(tag, "checkPachageToSend buffer length is " + receivedBuffer.length);
		int newLength = receivedBuffer.length + newData.length;
	   	 	byte[] tempBuffer = new byte[newLength];
			byteCopy(receivedBuffer, tempBuffer,0,0, receivedBuffer.length);
			byteCopy(newData, tempBuffer, 0,receivedBuffer.length, newData.length);
			receivedBuffer = null;
			receivedBuffer = tempBuffer;
	}else{
		Log.d(tag, "checkPachageToSend buffer is null !");
		receivedBuffer = new byte[newData.length];
		byteCopy(newData, receivedBuffer, 0,0, newData.length);
	}

   	 Log.d(tag, "buffer lenght now is " + receivedBuffer.length);
	if (receivedBuffer.length >= _lengthOfPackage) {
		//比设定的一个包的长度要长
		byte[] packageToSend = new byte[_lengthOfPackage];
		// 剩余内容
		byte[] tempBuffer = new byte[receivedBuffer.length - _lengthOfPackage];
		byteCopy(receivedBuffer, packageToSend, 0,0, _lengthOfPackage);
		byteCopy(receivedBuffer, tempBuffer, _lengthOfPackage,0, tempBuffer.length);
		receivedBuffer = null;
		receivedBuffer = tempBuffer;
		userCallback.didPackageReceived(currentPort, packageToSend);
		Log.d(tag, "left length is " + receivedBuffer.length);
	}


	}
	private void byteCopy(byte[] from,byte[] to,int fromIndex,int toIndex,int length){
		int realLength = (from.length<length)?from.length:length;
		for (int i = 0; i < realLength; i++) {
			to[i+toIndex] = from[i+fromIndex];
		}
	}


	//线程定时类
	Handler handler = new Handler() {
		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
		public void handleMessage(Message msg) {
		switch (msg.what) {
		case ACSUTILITY_SCAN_TIMEOUT_MSG:
			Log.d(tag, "scan time out");
			bScanning = false;
			mBtAdapter.stopLeScan(mLeScanCallback);
			if(userCallback != null)
					userCallback.didFinishedEnumPorts();
			break;

		default:
			break;
		}
		super.handleMessage(msg);
		}
	};
	private class myThread implements Runnable {
		@Override
		public void run(){
			//while (true) {
				try {
					Thread.sleep((long)_scanTime*1000);
					if (bScanning) {
						Message msg = new Message();
						msg.what = ACSUTILITY_SCAN_TIMEOUT_MSG;
						handler.sendMessage(msg);
					}
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			//}

		}
	};



	//接口类，用于外部实现与交互
	public interface IACSUtilityCallback{
		public void utilReadyForUse();
		//public void utilreadyForUse();
		public void didFoundPort(blePort newPort);//一定时间后发现端口
		public void didFinishedEnumPorts();
		public void didOpenPort(blePort port, Boolean bSuccess);
		public void didClosePort(blePort port);
		public void didPackageSended(boolean succeed);
		public void didPackageReceived(blePort port, byte[] packageToSend);
		public void heartbeatDebug();
	}

	//Port类
	public class blePort implements Serializable {
		public BluetoothDevice _device;
		
		public blePort(BluetoothDevice device) {
			_device = device;
		}
	}
	
	//debug function
	
	   public  void printHexString( byte[] b) { 
	    	for (int i = 0; i < b.length; i++) { 
		    	String hex = Integer.toHexString(b[i] & 0xFF);
			    	if (hex.length() == 1) { 
			    	hex = '0' + hex; 
			    	} 
			    Log.d(tag, hex);
	    	} 

	    }
}

