package com.quester.demo.barcode;

import com.quester.demo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Foreground activity that shows information about QR engine
 * @author John.Jian
 */
public class BarcodeActivity extends Activity {
	
	private final int RECEIVE = 0x1;
	private final int COMPLETE = 0x2;
	private final int UNCOMPLETE = 0x3;
	private final int TIMEOUT = 0x4;
	private final String UNKNOWN = "unknown";
	
	private TextView mInfoField;
	private TextView mVerField;
	private TextView mDateField;
	private TextView mSnField;
	private TextView mEsnField;
	private EditText mEsnEdit;
	private Button mEsnConfire;
	private RadioGroup mGroup;
	private RadioButton mTriggerRb;
	private RadioButton mSensorRb;
	private RadioButton mContinueRb;
	private ImageButton mButton;
	
	private boolean mConnected = false;
	private boolean mInitializing = false;
	private boolean mInitCompleted = false;
	private boolean mPausing = false;
	private boolean mEsnSetting = false;
	private boolean mModeSetting = false;
	private boolean mScreenOn = false;
	private int mReadMode = 0;
	
	private String mDevVer;
	private String mDevDate;
	private String mDevSn;
	private String mDevEsn;
	
	private SerialComm mComm;
	private CaptureRunnable mRunnable;
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	
	/* Handler for ui */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == RECEIVE) {
				byte[] data = (byte[])msg.obj;
				if (data.length == 1) {
					switch (data[0]) {
					case Command.SUCCESS:
						if (Status.trigging) {
							if (!Status.response) {
								Status.response = true;
								preventTimeout();
							}
						} else if (mEsnSetting) {
							setClickable(true);
							mEsnSetting = false;
							mEditor.putString("esn", mDevEsn);
							mEditor.commit();
							setScannerStatus();
						} else if (mModeSetting) {
							setClickable(true);
							mModeSetting = false;
							mEditor.putInt("mode", mReadMode);
							mEditor.commit();
						}
						break;
					case Command.FAILTURE:
						setClickable(true);
						if (mEsnSetting) {
							mEsnSetting = false;
							showToast(getString(R.string.barcode_esn)
									+ getString(R.string.barcode_setting_failed));
						} else if (mModeSetting) {
							mModeSetting = false;
							showToast(getString(R.string.barcode_scanner)
									+ getString(R.string.barcode_setting_failed));
						}
						break;
					case Command.DEV_REPLY:
						setClickable(true);
						handlerTrigger();
						break;
					default:
						setClickable(true);
						handlerTrigger();
						setBarcodeInfo(byteArrayToString(data));
						break;
					}
				} else {
					setClickable(true);
					if (data[0] == Command.PREFIX_RECV[0] 
							&& data[1] == Command.PREFIX_RECV[1]) {
						//query result, unrealized, refer Parser
						handlerTrigger();
					} else {
						handlerTrigger();
						setBarcodeInfo(byteArrayToString(data));
					}
				}
				data = null;
			} else if (msg.what == COMPLETE) {
				initLayout();
				setScannerStatus();
				if (mInitializing) {
					mInitializing = false;
				}
				Status.ready = true;
				startCapture();
				if (Status.trigging) {
					setClickable(false);
					mComm.triggerDown();
				}
			} else if (msg.what == UNCOMPLETE) {
				if (!mInitCompleted) {
					initLayout();
				}
				if (mInitializing) {
					mInitializing = false;
				}
				setBarcodeInfo(getString(R.string.barcode_disconnect));
			} else if (msg.what == TIMEOUT) {
				handlerTrigger();
				setClickable(true);
			}
		}
		
	};
	
	private void handlerTrigger() {
		if (Status.trigging) {
			Status.trigging = false;
			mComm.triggerUp();
		}
	}
	
	private String byteArrayToString(byte[] data) {
		return new String(data);
	}
	
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setProgressBar();
		mComm = new SerialComm(this);
		mRunnable = new CaptureRunnable();
		mPreferences = getSharedPreferences("scanner_status", Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}
	
	/* Show a progress bar until finish initialization */
	private void setProgressBar() {
		LinearLayout mLayout = new LinearLayout(this);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		mLayout.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams mLayoutParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT);
		
		ProgressBar mBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
		LinearLayout.LayoutParams mBarParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		
		TextView mField = new TextView(this);
		mField.setText(R.string.barcode_init);
		
		mLayout.addView(mBar, mBarParam);
		mLayout.addView(mField, mBarParam);
		setContentView(mLayout, mLayoutParam);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mInitializing) return;
		if (!mScreenOn) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mScreenOn = true;
		}
		
		String action = getIntent().getAction();
		if (action != null) {
			Status.trigging = action.equals(Status.ACTION_TRIGGER);
		}
		
		if (!mInitCompleted) {
			mInitializing = true;
			if (mComm.openSerial()) {
				mPausing = false;
				getSannerStatues();
				//observing initialization state
				new Thread(new Runnable() {
					public void run() {
						int counter = 10;
						while (!isFinishing() && !mPausing && !mInitCompleted && counter > 0) {
							try {
								if ((--counter) == 0) {
									mHandler.sendEmptyMessage(UNCOMPLETE);
									continue;
								}
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
			} else {
				initLayout();
				setBarcodeInfo(getString(R.string.disconnect));
				mInitializing = false;
			}
		} else {
			if (Status.trigging) {
				mComm.openSerial();
				startCapture();
				setClickable(false);
				mComm.triggerDown();
			} else {
				if (mComm.openSerial()) {
					mConnected = mComm.isConnected();
					if (mConnected) {
						startCapture();
					} else {
						mHandler.sendEmptyMessage(UNCOMPLETE);
					}
				} else {
					setBarcodeInfo(getString(R.string.disconnect));
				}
			}
		}
	}
	
	private void getSannerStatues() {
		new Thread(new Runnable() {
			public void run() {
				mConnected = mComm.isConnected();
				if (mConnected) {
					if (mPreferences.getBoolean("scanner", false)) {
						mDevVer = mPreferences.getString("ver", UNKNOWN);
						mDevDate = mPreferences.getString("date", UNKNOWN);
						mDevSn = mPreferences.getString("sn", UNKNOWN);
						mDevEsn = mPreferences.getString("esn", UNKNOWN);
						mReadMode = mPreferences.getInt("mode", 0);
						mHandler.sendEmptyMessage(COMPLETE);
					} else {
						mComm.writeSerial(Parser.getQueryCommand(Command.QUERY_DEV_VER));
						byte[] reply = mComm.readSerial();
						if (mPausing) return;
						if (reply != null) {
							mDevVer = Parser.getDevVersion(reply);
						}
						mComm.writeSerial(Parser.getQueryCommand(Command.QUERY_DEV_DATE));
						reply = mComm.readSerial();
						if (mPausing) return;
						if (reply != null) {
							mDevDate = Parser.getDevDate(reply);
						}
						mComm.writeSerial(Parser.getQueryCommand(Command.QUERY_DEV_SN));
						reply = mComm.readSerial();
						if (mPausing) return;
						if (reply != null) {
							mDevSn = Parser.getDevSn(reply);
						}
						mComm.writeSerial(Parser.getQueryCommand(Command.QUERY_DEV_ESN));
						reply = mComm.readSerial();
						if (mPausing) return;
						if (reply != null) {
							mDevEsn = Parser.getDevEsn(reply);
						}
						mComm.writeSerial(Parser.getQueryCommand(Command.QUERY_READ_MODE));
						reply = mComm.readSerial();
						if (mPausing) return;
						if (reply != null) {
							mReadMode = Parser.getReadMode(reply);
						}
						
						mEditor.putString("ver", (mDevVer != null) ? mDevVer : UNKNOWN);
						mEditor.putString("date", (mDevDate != null) ? mDevDate : UNKNOWN);
						mEditor.putString("sn", (mDevSn != null) ? mDevSn : UNKNOWN);
						mEditor.putString("esn", (mDevEsn != null) ? mDevEsn : UNKNOWN);
						mEditor.putInt("mode", mReadMode);
						mEditor.putBoolean("scanner", true);
						mEditor.commit();
						
						mHandler.sendEmptyMessage(COMPLETE);
					}
				} else {
					mHandler.sendEmptyMessage(UNCOMPLETE);
				}
			}
		}).start();
	}
	
	/* Master ui */
	private void initLayout() {
		setContentView(R.layout.activity_barcode);
		mInfoField = (TextView)findViewById(R.id.barcode_info);
		mVerField = (TextView)findViewById(R.id.barcode_ver);
		mDateField = (TextView)findViewById(R.id.barcode_date);
		mSnField = (TextView)findViewById(R.id.barcode_sn);
		mEsnField = (TextView)findViewById(R.id.barcode_esn);
		mEsnEdit = (EditText)findViewById(R.id.barcode_set_esn);
		mEsnConfire = (Button)findViewById(R.id.barcode_modify_esn);
		mGroup = (RadioGroup)findViewById(R.id.barcode_scanner);
		mTriggerRb = (RadioButton)findViewById(R.id.barcode_trigger);
		mSensorRb = (RadioButton)findViewById(R.id.barcode_sensor);
		mContinueRb = (RadioButton)findViewById(R.id.barcode_continue);
		mButton = (ImageButton)findViewById(R.id.barcode_capture);
		
		if (mReadMode == 0x30) {
			mTriggerRb.setChecked(true);
		} else if (mReadMode == 0x31) {
			mSensorRb.setChecked(true);
		} else if (mReadMode == 0x32) {
			mContinueRb.setChecked(true);
		}
		
		mEsnConfire.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!mConnected) return;
				handlerTrigger();
				setClickable(false);
				mDevEsn = mEsnEdit.getText().toString();
				mEsnSetting = true;
				mComm.setEsn(mDevEsn);
				mEsnEdit.setText("");
			}
		});
		mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (!mConnected) return;
				handlerTrigger();
				setClickable(false);
				mModeSetting = true;
				if (checkedId == mTriggerRb.getId()) {
					mComm.writeSerial(Parser.getSettingCommand(Command.MODE_TRIGGER));
					mReadMode = 0x30;
				} else if (checkedId == mSensorRb.getId()) {
					mComm.writeSerial(Parser.getSettingCommand(Command.MODE_SENSER));
					mReadMode = 0x31;
				} else if (checkedId == mContinueRb.getId()) {
					mComm.writeSerial(Parser.getSettingCommand(Command.MODE_CONTINUE));
					mReadMode = 0x32;
				}
			}
		});
		mButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!mConnected) return;
				setClickable(false);
				Status.response = false;
				Status.trigging = true;
				mComm.triggerDown();
			}
		});
		
		mInitCompleted = true;
	}
	
	private void preventTimeout() {
		new Thread(new Runnable() {
			public void run() {
				int counter = 50;
				while (!isFinishing() && Status.trigging && counter > 0) {
					try {
						Thread.sleep(100);
						if ((--counter) == 0) {
							mHandler.sendEmptyMessage(TIMEOUT);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void setClickable(boolean clickable) {
		mEsnConfire.setClickable(clickable);
		mTriggerRb.setClickable(clickable);
		mSensorRb.setClickable(clickable);
		mContinueRb.setClickable(clickable);
		mButton.setClickable(clickable);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mScreenOn) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mScreenOn = false;
		}
		if (mInitializing) {
			mInitializing = false;
		}
		if (!mInitCompleted) {
			mPausing = true;
		}
		if (mConnected) {
			stopCapture();
		}
		mComm.closeSerial();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Status.ready = false;
		mComm = null;
		mRunnable = null;
	}
	
	private void setBarcodeInfo(String str) {
		mInfoField.setText(str);
	}
	
	private void setScannerStatus() {
		mVerField.setText(getString(R.string.barcode_ver) + 
				(mDevVer != null ? mDevVer : UNKNOWN));
		mDateField.setText(getString(R.string.barcode_date) + 
				(mDevDate != null ? mDevDate : UNKNOWN));
		mSnField.setText(getString(R.string.barcode_sn) + 
				(mDevSn != null ? mDevSn : UNKNOWN));
		mEsnField.setText(getString(R.string.barcode_esn) + 
				(mDevEsn != null ? mDevEsn : UNKNOWN));
	}
	
	@SuppressWarnings("unused")
	private void resetFactory() {
		if (!mConnected) return;
		handlerTrigger();
		stopCapture();
		mComm.resetFactory();
		mEditor.putBoolean("scanner", false);
		mEditor.commit();
		mInitCompleted = false;
		Status.ready = false;
		setProgressBar();
		
		mInitializing = true;
		mPausing = false;
		getSannerStatues();
		//observing initialization state
		new Thread(new Runnable() {
			public void run() {
				int counter = 10;
				while (!isFinishing() && !mPausing && !mInitCompleted && counter > 0) {
					try {
						if ((--counter) == 0) {
							mHandler.sendEmptyMessage(UNCOMPLETE);
							continue;
						}
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void startCapture() {
		mRunnable.setConnectState(true);
		mComm.enableSetupCode();
		new Thread(mRunnable).start();
	}
	
	private void stopCapture() {
		mRunnable.setConnectState(false);
		mComm.disableSetupCode();
	}
	
	/** Monitoring thread */
	private class CaptureRunnable implements Runnable {
		
		private boolean isConnected;
		
		public void setConnectState(boolean connect) {
			isConnected = connect;
		}
		
		private void readSerialPort() {
			byte[] recvBuf = null;
			while (isConnected) {
				recvBuf = mComm.readSerial();
				if (recvBuf != null) {
					mHandler.sendMessage(mHandler.obtainMessage(RECEIVE, recvBuf));
				}
			}
		}

		@Override
		public void run() {
			readSerialPort();
		}
		
	}

}
