package com.quester.demo.infrared;

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
import android.widget.TextView;

/**
 * Foreground activity that shows information about Infrared engine
 * @author John.Jian
 *
 */
public class InfraredActivity extends Activity {
	
	private final int RECEIVED = 1;
	
	private TextView mRecvField;
	private TextView mSendField;
	private EditText mSendMsg;
	private Button mSendingBtn;
	
	private final String[] items = {"2400", "1200"};
	private final int[] bandrates = {2400, 1200};
	private int checkedItem = 0;
	private boolean mConnected = false;
	private boolean mScreenOn = false;
	private SerialComm mComm;
	private CaptureRunnable mRunnable;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == RECEIVED) {
				byte[] data = (byte[])msg.obj;
				mRecvField.append(getNewLine(new String(data)));
			}
		}
	};
	
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
		mSendMsg = (EditText)findViewById(R.id.infrared_msg);
		mSendingBtn = (Button)findViewById(R.id.infrared_sending);
		mSendingBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mConnected) {
					String msg = mSendMsg.getText().toString();
					mComm.writeSerial(msg.getBytes());
					mSendMsg.setText("");
					mSendField.append(getNewLine(msg));
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!mScreenOn) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mScreenOn = true;
		}
		mConnected = mComm.openSerial(bandrates[checkedItem]);
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
		}
		mComm.closeSerial();
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
					if (mComm.openSerial(bandrates[which])) {
						checkedItem = which;
						mRecvField.append("\nbandrate: " + bandrates[which] + "\n");
					} else {
						mRecvField.setText(R.string.disconnect);
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
