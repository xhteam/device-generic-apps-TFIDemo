package com.quester.demo.headset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.quester.demo.R;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class HeadsetObserver extends Service {
	
	private static final String NODE_PATH = "/proc/driver/uartswitcher";
	
	private AlertDialog mDialog;
	private Context mContext;
	private boolean mNodeState = false;
	private boolean mRegister = false;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			setHeadsetState(intent.getIntExtra("state", 0) == 1);
		}
	};
	
	private boolean checkNodeState() {
		boolean ret = false;
		File file = new File(NODE_PATH);
		if (file.exists() && file.canWrite()) {
			ret = true;
		}
		return ret;
	}
	
	private void setMode(boolean flag) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(NODE_PATH));
			if (flag) {
				// switch to uart mode
				fos.write(1);
			} else {
				// switch to hp mode
				fos.write(0);
			}
			fos.flush();
		} catch (IOException e) {
			// NA
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// NA
				}
			}
		}
	}
	
	private void setHeadsetState(boolean state) {
		if (state) {
			if (mDialog == null) {
				mDialog = new AlertDialog.Builder(this).
						setTitle(android.R.string.dialog_alert_title).
						setMessage(mContext.getString(R.string.headset_prompt)).
						setPositiveButton(android.R.string.yes, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								setMode(true);
							}
						}).
						setNegativeButton(android.R.string.no, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// NA
							}
						}).
						create();
				mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				mDialog.setCancelable(false);
			}
			mDialog.show();
		} else {
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
			}
			//setMode(false);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mNodeState = checkNodeState();
		if (mNodeState) {
			SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
			if (spf.getBoolean("headset_prompt", true)) {
				IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
				registerReceiver(mReceiver, filter);
				mRegister = true;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mRegister) {
			unregisterReceiver(mReceiver);
			mRegister = false;
		}
		mDialog = null;
		mContext = null;
		Intent intent = new Intent();
		intent.setClass(this, HeadsetObserver.class);
		this.startService(intent);
	}
	
}
