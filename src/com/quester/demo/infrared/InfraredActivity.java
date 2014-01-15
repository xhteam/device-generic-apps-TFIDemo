package com.quester.demo.infrared;

import java.io.UnsupportedEncodingException;
import com.quester.android.platform_library.infrared.*;

import com.quester.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Foreground activity that shows information about Infrared engine
 * @author John.Jian
 *
 */
public class InfraredActivity extends Activity {
	
	private final int RECEIVED = 1;
	private final int BAUDRATE = 2;
	
	private TextView mRecvField;
	private TextView mSendField;
	private ScrollView mScRecv;
	private ScrollView mScSend;
	private EditText mSendMsg;
	private Button mSendingBtn;
	
	private final String[] items = {"1200", "2400"};
	private final int[] baudrates = {1200, 2400};
	private int checkedItem = 0;
	private boolean mConnected = false;
	private boolean mScreenOn = false;
	private SerialComm mComm;
	private CaptureRunnable mRunnable;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == RECEIVED) {
				byte[] data = (byte[])msg.obj;
				if (data != null) {
					try {
						String str = new String(data, "utf-8");
						mRecvField.append(str);
					} catch (UnsupportedEncodingException e) {
						return;
					}
				}
				mHandler.post(mScrollRecv);
			} else if (msg.what == BAUDRATE) {
				String baud = getString(R.string.bandrate) + baudrates[checkedItem] 
					+ getString(R.string.action_settings);
				if (msg.arg1 == 1) {
					showToast(baud + getString(R.string.settings_success));
				} else {
					showToast(baud + getString(R.string.settings_fail));
				}
			}
		}
	};

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	private String getNewLine(String msg) {
		return (msg + "\n");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infrared);
		initLayout();
		mComm = new SerialComm(this);
		mRunnable = new CaptureRunnable();
	}
	
	private void initLayout() {
		mRecvField = (TextView)findViewById(R.id.infrared_recv);
		mSendField = (TextView)findViewById(R.id.infrared_send);
		mScRecv = (ScrollView)findViewById(R.id.infrared_recv_field);
		mScSend = (ScrollView)findViewById(R.id.infrared_send_field);
		mSendMsg = (EditText)findViewById(R.id.infrared_msg);
		mSendingBtn = (Button)findViewById(R.id.infrared_sending);
		mSendingBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mConnected) {
					String msg = mSendMsg.getText().toString();
					if (msg.length() > 0) {
						try {
							mComm.writeSerial(msg.getBytes("utf-8"));
							mSendMsg.setText("");
							mSendField.append(getNewLine(msg));
							mHandler.post(mScrollSend);
						} catch (UnsupportedEncodingException e) {
							return;
						}
					}
				}
			}
		});
	}

	private Runnable mScrollRecv = new Runnable() {
		public void run() {
			int off = mRecvField.getMeasuredHeight() - mScRecv.getHeight();
			if (off > 0) {
				mScRecv.scrollTo(0, off);
			}
		}
	};

	private Runnable mScrollSend = new Runnable() {
		public void run() {
			int off = mSendField.getMeasuredHeight() - mScSend.getHeight();
			if (off > 0) {
				mScSend.scrollTo(0, off);
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!mScreenOn) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mScreenOn = true;
		}
		mConnected = mComm.openSerial(baudrates[checkedItem]);
		if (mConnected) {
			mRunnable.setConnect(true);
			new Thread(mRunnable).start();
		} else {
			mRecvField.setText(R.string.disconnect);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mScreenOn) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mScreenOn = false;
		}
		if (mConnected) {
			mRunnable.setConnect(false);
			mComm.closeSerial();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_infrared, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.infrared_settings) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.bandrate);
			builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (mConnected) {
						mRunnable.setConnect(false);
						mComm.closeSerial();
					}
					checkedItem = which;
					mConnected = mComm.openSerial(baudrates[checkedItem]);
					if (mConnected) {
						mRunnable.setConnect(true);
						new Thread(mRunnable).start();
						mHandler.sendMessage(mHandler.obtainMessage(BAUDRATE, 1, 1));
					} else {
						mRecvField.setText(R.string.disconnect);
						mHandler.sendMessage(mHandler.obtainMessage(BAUDRATE, 0, 0));
					}
					dialog.dismiss();
				}
			});
			builder.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create();
			builder.setCancelable(false);
			builder.show();
		}
		return true;
	}
	
	private class CaptureRunnable implements Runnable {
		volatile boolean isConnect;
		
		public void setConnect(boolean connect) {
			isConnect = connect;
		}

		public void run() {
			while(isConnect) {
				byte[] data = mComm.readSerial();
				if (data != null) {
					mHandler.sendMessage(mHandler.obtainMessage(RECEIVED, data));
				}
			}
		}
		
	}
	
}
