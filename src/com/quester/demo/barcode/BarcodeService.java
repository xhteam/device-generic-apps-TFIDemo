package com.quester.demo.barcode;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/**
 * QR engine background service, hardware button F1&F2 observer
 * @author John.Jian
 */
public class BarcodeService extends Service implements OnKeyListener {
	
	private final String TAG = this.getClass().getName();
	private final IBinder mBinder = new BarcodeBinder();
	private long timeForKeycodeF1 = 0;
	private long timeForKeycodeF2 = 0;
	
	public final String ACTION_TRIGGER = "action.barcode.trigger";
	public boolean mResponse = false;
	public boolean mTrigging = false;
	public boolean mUiReady = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Started barcode hardware button listener");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Stop barcode hardware button listener");
	}
	
	/*
	 * QR engine reset path: /sys/class/gpio/gpio72/value
	 * Reset: 0 to reset, 1 as normal
	 * QR engine trigger path: /sys/class/gpio/gpio94/value
	 * Trigger: 0 at least 10ms to trig scan, 1 as normal
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (!mTrigging) {
			if (keyCode == KeyEvent.KEYCODE_F1) {
				timeForKeycodeF1 = System.currentTimeMillis();
			} else if (keyCode == KeyEvent.KEYCODE_F2) {
				timeForKeycodeF2 = System.currentTimeMillis();
			}
			
			if (Math.abs(timeForKeycodeF1 - timeForKeycodeF2) < 500) {
				mResponse = false;
				mTrigging = true;
				Intent intent = new Intent();
				intent.setAction(ACTION_TRIGGER);
				sendBroadcast(intent);
				requestQR();
			}
			return true;
		}
		return false;
	}
	
	private void requestQR() {
		new Thread(new Runnable() {
			public void run() {
				try {
					int counter = 10;
					while (!mUiReady && counter > 0) {
						Thread.sleep(100);
					}
					if (mUiReady) {
						counter = 30; //waiting response for 3000ms
						while (!mResponse && counter > 0) {
							Thread.sleep(100);
						}
						if (mResponse) {
							counter = 50;
							while (mTrigging && counter > 0) {
								Thread.sleep(100);
							}
						}
						if (mTrigging) {
							mTrigging = false;
							Log.i(TAG, "QR trigger fail");
						}
					} else {
						mTrigging = false;
						Log.i(TAG, "QR engine incomplete initialization");
					}
				} catch (InterruptedException e) {
					Log.e(TAG, "requestQR, " + e.getMessage());
				}
			}
		}).start();
	}
	
	public class BarcodeBinder extends Binder {
		BarcodeService getService() {
			return BarcodeService.this;
		}
	}
	
}
