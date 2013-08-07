package com.quester.demo.barcode;

import com.quester.demo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class BarcodeActivity extends Activity implements OnClickListener {
	
	private static final int RECEIVE = 0x1;
	private static final int COMPLETE = 0x2;
	private static final int UNCOMPLETE = 0x3;
	private static final int SUCCESS = 0x4;
	private static final int FAILTURE = 0x5;
	private static final int REOPEN = 0x6;
	
	private TextView mInfo;
	private TextView mVer;
	private TextView mDate;
	private TextView mSn;
	private TextView mEsn;
	private EditText mEdit;
	private Button mConfire;
	private Button mReset;
	private RadioGroup mGroup;
	private RadioButton mTriggerRb;
	private RadioButton mSensorRb;
	private RadioButton mContinueRb;
	
	private SerialComm mComm;
	private CaptureRunnable mRunnable;
	
	private static boolean isInitCompleted;
	private static boolean mConnected;
	private static boolean isDone;
	
	private String mDevVer;
	private String mDevDate;
	private String mDevSn;
	private String mDevEsn;
	
	/* Listen serial port */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == RECEIVE) {
				byte[] data = (byte[])msg.obj;
				if (data.length == 1) {
					if (data[0] == Command.SUCCESS) {
						showToast(true);
					} else if (data[0] == Command.FAILTURE) {
						showToast(false);
					} else {
						setBarcodeInfo(byteArrayToString(data));
					}
				} else {
					if (data[0] == Command.PREFIX_RECV[0] 
							&& data[1] == Command.PREFIX_RECV[1]) {
						// query result, unrealized, reference class Parser
					} else {
						// display barcode info
						setBarcodeInfo(byteArrayToString(data));
					}
				}
				data = null;
			} else if (msg.what == COMPLETE) {
				isInitCompleted = true;
				initLayout();
				setDevVer();
				setDevDate();
				setDevSn();
				setDevEsn();
				startCaptureListener();
			} else if (msg.what == UNCOMPLETE) {
				initLayout();
				setBarcodeInfo(getString(R.string.unknown_dev));
			} else if (msg.what == SUCCESS) {
				showToast(true);
				startCaptureListener();
			} else if (msg.what == FAILTURE) {
				showToast(false);
				startCaptureListener();
			} else if (msg.what == REOPEN) {
				showToast(false);
				mComm.openSerial();
				startCaptureListener();
			}
		}
		
	};
	
	/* Show setting result */
	private void showToast(boolean bool) {
		if (bool) {
			Toast.makeText(this, Utils.success, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, Utils.failture, Toast.LENGTH_SHORT).show();
		}
	}
	
	/* Default charset utf-8 */
	private String byteArrayToString(byte[] data) {
		return new String(data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setProgressUi();
		firstInitialize();
	}
	
	private void setProgressUi() {
		LinearLayout mLayout = new LinearLayout(this);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		mLayout.setGravity(Gravity.CENTER);
		LayoutParams mLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT);
		
		ProgressBar mBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
		LayoutParams mBarParam = new LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT);
		
		mLayout.addView(mBar, mBarParam);
		setContentView(mLayout, mLayoutParam);
	}
	
	private void firstInitialize() {
		Utils.success = getString(R.string.setting_success);
		Utils.failture = getString(R.string.setting_failture);
		Utils.unknown = getString(R.string.unknown);
		
		isInitCompleted = false;
		mConnected = false;
		mDevVer = mDevDate = mDevSn = mDevEsn = null;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mComm = new SerialComm(this);
		mRunnable = new CaptureRunnable();
		
		if (mComm.openSerial()) {
			mConnected = mComm.isConnected();
			if (!isInitCompleted) {
				if (mConnected) {
					watchThread(getDevInfoThread(), 5000);
				} else {
					initLayout();
					setBarcodeInfo(getString(R.string.unknown_dev));
				}
			} else {
				if (mConnected) {
					startCaptureListener();
				} else {
					setBarcodeInfo(getString(R.string.unknown_dev));
				}
			}
		} else {
			if (!isInitCompleted) {
				initLayout();
			}
			setBarcodeInfo(getString(R.string.unknown_dev));
		}
	}
	
	private Thread getDevInfoThread() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				mComm.writeSerial(Command.getQueryCommand(Command.QUERY_DEV_VER));
				byte[] reply = mComm.readSerial();
				if (reply != null) {
					mDevVer = Parser.getDevVersion(reply);
				}
				mComm.writeSerial(Command.getQueryCommand(Command.QUERY_DEV_DATE));
				reply = mComm.readSerial();
				if (reply != null) {
					mDevDate = Parser.getDevDate(reply);
				}
				mComm.writeSerial(Command.getQueryCommand(Command.QUERY_DEV_SN));
				reply = mComm.readSerial();
				if (reply != null) {
					mDevSn = Parser.getDevSn(reply);
				}
				mComm.writeSerial(Command.getQueryCommand(Command.QUERY_DEV_ESN));
				reply = mComm.readSerial();
				if (reply != null) {
					mDevEsn = Parser.getDevEsn(reply);
				}
				getEndSuffix();
				mHandler.sendEmptyMessage(COMPLETE);
			}
		});
		thread.start();
		return thread;
	}
	
	private void watchThread(final Thread thread, final long maxDelay) {
		new Thread(new Runnable() {
			public void run() {
				long curTime = System.currentTimeMillis();
				while ((System.currentTimeMillis() - curTime) <= maxDelay) {
					Utils.delay(1000);
					if (isInitCompleted) return;
				}
				mComm.closeSerial();
				mConnected = false;
				mHandler.sendEmptyMessage(UNCOMPLETE);
				Log.i(Utils.TAG, "error operation");
			}
		}).start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mConnected) {
			stopCaptureListener();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initLayout() {
		setContentView(R.layout.activity_barcode);
		mInfo = (TextView)findViewById(R.id.barcode_info);
		mVer = (TextView)findViewById(R.id.barcode_ver);
		mDate = (TextView)findViewById(R.id.barcode_date);
		mSn = (TextView)findViewById(R.id.barcode_sn);
		mEsn = (TextView)findViewById(R.id.barcode_esn);
		mEdit = (EditText)findViewById(R.id.barcode_set_esn);
		mConfire = (Button)findViewById(R.id.barcode_modify_esn);
		mGroup = (RadioGroup)findViewById(R.id.barcode_scanner);
		mTriggerRb = (RadioButton)findViewById(R.id.barcode_trigger);
		mSensorRb = (RadioButton)findViewById(R.id.barcode_sensor);
		mContinueRb = (RadioButton)findViewById(R.id.barcode_continue);
		mReset = (Button)findViewById(R.id.barcode_reset);
		
		if (isInitCompleted) {
			mConfire.setOnClickListener(this);
			mReset.setOnClickListener(this);
			
			mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if (!mConnected) return;
					stopCaptureListener();
					mComm.openSerial();
					if (checkedId == mTriggerRb.getId()) {
						setReadMode(Utils.MODE_TRIGGER, 1800);
					} else if (checkedId == mSensorRb.getId()) {
						setReadMode(Utils.MODE_SENSOR, 1800);
					} else if (checkedId == mContinueRb.getId()) {
						setReadMode(Utils.MODE_CONTINUE, 1800);
					}
				}
			});
		}
	}
	
	private void setReadMode(final int mode, final int maxDelay) {
		isDone = false;
		final Thread thread = new Thread(new Runnable() {
			public void run() {
				if (mComm.setReadMode(mode)) {
					mHandler.sendEmptyMessage(SUCCESS);
				} else {
					mHandler.sendEmptyMessage(FAILTURE);
				}
				isDone = true;
			}
		});
		thread.start();
		
		new Thread(new Runnable() {
			public void run() {
				long curTime = System.currentTimeMillis();
				while ((System.currentTimeMillis() - curTime) <= maxDelay) {
					Utils.delay(Command.MAX_DELAY);
					if (isDone) return;
				}
				mComm.closeSerial();
				mHandler.sendEmptyMessage(REOPEN);
			}
		}).start();
	}
	
	@Override
	public void onClick(View v) {
		if (!mConnected) return;
		switch (v.getId()) {
		case R.id.barcode_modify_esn:
			String newEsn = mEdit.getText().toString();
			if (newEsn != null) {
				stopCaptureListener();
				mComm.openSerial();
				if (mComm.setEsn(newEsn)) {
					mDevEsn = newEsn;
					setDevEsn();
				}
				mEdit.setText("");
				startCaptureListener();
			}
			break;
		case R.id.barcode_reset:
			stopCaptureListener();
			mComm.openSerial();
			if (mComm.resetFactoryData()) {
				mComm.closeSerial();
				setProgressUi();
				isInitCompleted = false;
				onResume();
			} else {
				startCaptureListener();
			}
			break;
		default:
			break;
		}
	}
	
	private void getEndSuffix() {
		mComm.writeSerial(Command.getQueryCommand(Command.QUERY_END_CHAR));
		byte[] reply = mComm.readSerial();
		if (reply != null) {
			Parser.getEndChars(reply);
		} else {
			Parser.endChars = false;
			Parser.endCharsBuf = null;
		}
	}
	
	/* Barcode information */
	private void setBarcodeInfo(String str) {
		mInfo.setText(str);
	}
	
	/* Firmware version */
	private void setDevVer() {
		mVer.setText(getString(R.string.barcode_ver) + 
				(mDevVer != null ? mDevVer : Utils.unknown));
	}
	
	/* Manufacture date */
	private void setDevDate() {
		mDate.setText(getString(R.string.barcode_date) + 
				(mDevDate != null ? mDevDate : Utils.unknown));
	}
	
	/* Device serial number */
	private void setDevSn() {
		mSn.setText(getString(R.string.barcode_sn) + 
				(mDevSn != null ? mDevSn : Utils.unknown));
	}
	
	/* Device electronic serial number */
	private void setDevEsn() {
		mEsn.setText(getString(R.string.barcode_esn) + 
				(mDevEsn != null ? mDevEsn : Utils.unknown));
	}
	
	private void startCaptureListener() {
		mRunnable.setConnectState(true);
		new Thread(mRunnable).start();
	}
	
	private void stopCaptureListener() {
		mRunnable.setConnectState(false);
		mComm.closeSerial();
	}
	
	/** Monitoring thread */
	private class CaptureRunnable implements Runnable {
		
		private byte[] captureBuf;
		private boolean isConnected;
		
		public CaptureRunnable() {
			captureBuf = new byte[Utils.BUF_LENGTH];
		}
		
		public void setConnectState(boolean connect) {
			isConnected = connect;
		}
		
		private void getDataStream() {
			byte[] tmpBuf = null;
			int tmpLens;
			while (isConnected) {
				tmpBuf = mComm.readSerial();
				if (tmpBuf != null) {
					tmpLens = tmpBuf.length;
					System.arraycopy(tmpBuf, 0, captureBuf, 0, tmpLens);
					if (Parser.endChars) {
						if (tmpLens < Parser.endCharsLens) {
							tmpBuf = mComm.readSerial();
							if (tmpBuf != null)
								System.arraycopy(tmpBuf, 0, captureBuf, tmpLens, tmpBuf.length);
						} else {
							if (Parser.endCharsLens == 2) {
								if (tmpBuf[tmpLens-1]!=Parser.endCharsBuf[1]
										|| tmpBuf[tmpLens-2]!=Parser.endCharsBuf[0]) {
									tmpBuf = mComm.readSerial();
									if (tmpBuf != null)
										System.arraycopy(tmpBuf, 0, captureBuf, tmpLens, tmpBuf.length);
								}
							} else {
								if (tmpBuf[tmpLens-1]!=Parser.endCharsBuf[0]) {
									tmpBuf = mComm.readSerial();
									if (tmpBuf != null)
										System.arraycopy(tmpBuf, 0, captureBuf, tmpLens, tmpBuf.length);
								}
							}
						}
					}
					Message msg = mHandler.obtainMessage(RECEIVE, captureBuf);
					mHandler.sendMessage(msg);
				}
			}
		}

		@Override
		public void run() {
			getDataStream();
		}
		
	}

}
