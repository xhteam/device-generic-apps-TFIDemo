package com.quester.demo.barcode;

import com.quester.demo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class NewBarcodeActivity extends Activity{

	private static final String TAG = "NewBarcodeActivity";
	public static final String BARCODE_PATH = "/proc/driver/barcode";
	private TextView txt_info;
	private TextView txt_decode_ver;
	private TextView txt_framework_ver;
	private TextView txt_soft_ver;
	private ImageButton button;
	
	private RadioGroup mGroup;
	private RadioButton mTriggerRb;
	private RadioButton mContinueRb;
	
	private NewSerialComm mComm;
	
	//
	private int mStatus;
	private static final int STATUS_HOST = 0;
	private static final int STATUS_CONTINUE = 1;
	
	//
	private final int RECEIVE = 0x1;
	private final int TIMEOUT = 0x2;
	
	//
	private Thread ioThread;
	private CaptureRunnable mRunnable;
	
	private Handler mHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
			case RECEIVE:
				byte[] data = (byte[])msg.obj;

				if (data != null)
				{
					String reply = new String(data);
					String validCmd = new String(NewParser.VALID_COMMAND);
					String invalidCmd = new String(NewParser.INVALID_COMMAND);
					String invalidValue = new String(NewParser.INVALID_VALUE);
//					int len = data.length;
//					byte response = data[len - 1];
//					if (response == NewParser.VALID_COMMAND || response == NewParser.INVALID_COMMAND || response == NewParser.INVALID_VALUE)
					if (reply.contains(validCmd) || reply.contains(invalidCmd) || reply.contains(invalidValue))
					{
						Log.i(TAG, "rsp cmd : " + new String(data));		
					}
					else	//barcode data
					{
						txt_info.setText(new String(data));
						if (mStatus == STATUS_HOST)
						{
							Status.trigging = false;
							setClickable(true);
						}
					}						
				}
				else
				{
					Log.e(TAG, "receive null msg");
				}
				setClickable(true);
				break;
			case TIMEOUT:
				Status.trigging = false;
				setClickable(true);
				break;
			default:
				Log.e(TAG, "handler get error message");
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_new_barcode);		
		mComm = new NewSerialComm(this);
		if (mComm.ifPowerOn())
		{
			Log.i(TAG, "barcode power is on");
		}
		else
		{
			Log.i(TAG, "barcode power is off");
			mComm.turnOnPower();
		}
		
		mComm.openSerial();
		mRunnable = new CaptureRunnable();
		
		initViews();
		if (initBarcode())
		{
			initValues();
			
		}
	}
	
	private void initValues()
	{
		mStatus = STATUS_HOST;
		mGroup.check(R.id.barcode_trigger);
	}
	
	private boolean initBarcode()
	{	
		String[] info = mComm.isConnected();
		if (info != null)
		{
			txt_decode_ver.append(info[0]);
			txt_framework_ver.append(info[1]);
			txt_soft_ver.append(info[2]);
		}
		else
		{
			createInitFailDialog();
//			Toast.makeText(this, R.string.barcode_init_failed, Toast.LENGTH_LONG).show();
			Log.e(TAG, "init barcode failed!");
			return false;
		}
		return true;
	}
	
	private void createInitFailDialog()
	{
		AlertDialog.Builder fail = new AlertDialog.Builder(this);
		fail.setIcon(R.drawable.error)
			.setTitle(R.string.error)
			.setMessage(R.string.barcode_init_failed)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.create()
			.show();
	}
	
	private void initViews()
	{
		txt_info = (TextView) findViewById(R.id.barcode_info);
		txt_decode_ver = (TextView) findViewById(R.id.txt_decode_ver);
		txt_framework_ver = (TextView) findViewById(R.id.txt_framework_ver);
		txt_soft_ver = (TextView) findViewById(R.id.txt_soft_ver);
		
		mGroup = (RadioGroup)findViewById(R.id.barcode_scanner);
		mTriggerRb = (RadioButton)findViewById(R.id.barcode_trigger);
		mContinueRb = (RadioButton)findViewById(R.id.barcode_continue);
		
		mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == mTriggerRb.getId()) {
					mComm.writeSerial(NewParser.getCommand(NewParser.TRIGGER_MODE_HOST));
					button.setEnabled(true);
					mStatus = STATUS_HOST;
				} else if (checkedId == mContinueRb.getId()) {
					mComm.writeSerial(NewParser.getCommand(NewParser.TRIGGER_MODE_CONTINUE));
					button.setEnabled(false);
					mStatus = STATUS_CONTINUE;
				}
			}
		});
		
		button = (ImageButton) findViewById(R.id.barcode_capture);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				trigBarcode();
//				mComm.turnOffPower();	//test code
			}
		});
	}
	
	private void trigBarcode()
	{
		Status.trigging = true;
		mComm.writeSerial(NewParser.START_DECODE);
		startScan();
		setClickable(false);
	}
	
	private void startScan()
	{
		new Thread(new Runnable() {
			public void run() {
				int counter = 40;
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
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mComm != null)
			mComm.openSerial();
		startCapture();
		Intent i = getIntent();
		if (Status.ACTION_NEW_TRIGGER.equals(i.getAction()))
		{
			if (mStatus == STATUS_CONTINUE)
			{
				mComm.writeSerial(NewParser.getCommand(NewParser.TRIGGER_MODE_HOST));
				button.setEnabled(true);
				mStatus = STATUS_HOST;
			}
			trigBarcode();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		try {
			stopCapture();	
			ioThread.join();
			mComm.closeSerial();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mComm = null;
		mRunnable = null;
	}
	
	private void setClickable(boolean clickable) 
	{
		mTriggerRb.setClickable(clickable);
		mContinueRb.setClickable(clickable);
		button.setClickable(clickable);
	}	
	
	
	private void startCapture() {
		mRunnable.setConnectState(true);
		ioThread = new Thread(mRunnable);
		ioThread.start();
	}
	
	private void stopCapture() {
		mRunnable.setConnectState(false);
		mComm.writeSerial(NewParser.getCommand(NewParser.FIRMWAER_VERSION_LIST.getBytes()));
	}
	
	private class CaptureRunnable implements Runnable {
		
		private boolean isConnected;
		
		public void setConnectState(boolean connect) {
			isConnected = connect;
		}
		
		private void readSerialPort() {
			byte[] recvBuf = null;
			while (isConnected) {
				if (mComm != null)
					recvBuf = mComm.readSerial();
				else
					break;
				
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
