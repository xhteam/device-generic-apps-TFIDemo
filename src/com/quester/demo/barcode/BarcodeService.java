package com.quester.demo.barcode;

import java.io.File;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * QR engine background service
 * @author John.Jian
 */
public class BarcodeService extends Service {
	
	/*
	 * QR engine reset path: /sys/class/gpio/gpio191/value
	 * Reset: 0 to reset, 1 as normal
	 * QR engine trigger path: /sys/class/gpio/gpio94/value
	 * Trigger: 0 at least 10ms to trig scan, 1 as normal
	 */
	
	private final String TAG = "BarcodeService";
	private final String ACTION_F1 = "action.KEYCODE_F1";
	private final String ACTION_F2 = "action.KEYCODE_F2";
	private long timeForKeycodeF1 = 0;
	private long timeForKeycodeF2 = 0;
	private KeyReceiver mReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mReceiver = new KeyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_F1);
		filter.addAction(ACTION_F2);
		registerReceiver(mReceiver, filter);
		Log.i(TAG, "Started barcode key listener");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		Log.i(TAG, "Stop barcode key listener");
	}
	
	private void requestQR() {
		new Thread(new Runnable() {
			public void run() {
				try {
					int counter = 10;
					while (!Status.ready && counter > 0) {
						Thread.sleep(100);
						counter--;
					}
					if (Status.ready) {
						counter = 30; //waiting response for 3000ms
						while (!Status.response && counter > 0) {
							Thread.sleep(100);
							counter--;
						}
						if (Status.response) {
							counter = 50;
							while (Status.trigging && counter > 0) {
								Thread.sleep(100);
								counter--;
							}
						}
						if (Status.trigging) {
							Status.trigging = false;
							Log.i(TAG, "QR trigger fail");
						}
					} else {
						Status.trigging = false;
						Log.i(TAG, "QR engine incomplete initialization");
					}
				} catch (InterruptedException e) {
					Log.e(TAG, "requestQR, " + e.getMessage());
				}
			}
		}).start();
	}
	
	private void sendIntent() {
		Intent intent = new Intent(Status.ACTION_TRIGGER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		startActivity(intent);
	}
	
	private boolean checkVersion()
	{
		File file = new File(NewBarcodeActivity.BARCODE_PATH);
		if (file.exists())
		{
			Log.i(TAG, "new barcode exist");
			return true;
		}
		else
		{
			Log.i(TAG, "new barcode not exist");
			return false;
		}
	}
	
	class KeyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context cotext, Intent intent) {
			Log.d(TAG, "get key broadcast triggering="+Status.trigging+",BUTTON_TRIGGER="+Status.BUTTON_TRIGGER);	//wangxi
			
			if (!Status.trigging && Status.BUTTON_TRIGGER) 
			{				
				String action = intent.getAction();
				if (action.equals(ACTION_F1)) 
				{
					timeForKeycodeF1 = System.currentTimeMillis();
				} 
				else if (action.equals(ACTION_F2)) 
				{
					timeForKeycodeF2 = System.currentTimeMillis();
				} 
				else 
				{
					//
				}
				
				if (Math.abs(timeForKeycodeF1 - timeForKeycodeF2) < 500) 
				{
					Status.BUTTON_TRIGGER = false;
					if (checkVersion())	
					{
						Log.i(TAG, "new barcode");	//wangxi
						//new barcode						
						if (Status.BARCODE_ACTIVITY == Status.ACTIVITY_ON)
						{
							Log.d(TAG, "send broadcast");	//wangxi
							Intent i = new Intent(Status.ACTION_NEW_TRIGGER_BROADCAST);
							sendBroadcast(i);
						}
//						else if (Status.BARCODE_ACTIVITY == Status.ACTIVITY_PAUSE)
//						{
//							Log.d(TAG, "wait 1s and start activity");	//wangxi
//							
//							try {
//								Thread.sleep(1200);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							
//							Intent i = new Intent(Status.ACTION_NEW_TRIGGER);
//							i.putExtra(Status.EXTRA_TRIGGER_ONCE, true);
//							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//							startActivity(i);
//							Log.i(TAG, "send activity success");	//wangxi
//						}
						else if (Status.BARCODE_ACTIVITY == Status.ACTIVITY_OFF)
						{
							Log.d(TAG, "start activity");	//wangxi
							Intent i = new Intent(Status.ACTION_NEW_TRIGGER);
							i.putExtra(Status.EXTRA_TRIGGER_ONCE, true);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(i);
						}
						else
						{
							Log.e(TAG, "get error status.");
						}						
					}
					else
					{
						Status.response = false;
						Status.trigging = true;
						sendIntent();
						requestQR();
					}
					
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}
		}
		
	}
	
	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) 
			{
			case 1: 
				try {
					Thread.sleep(300);
					Status.BUTTON_TRIGGER = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};
	
}
