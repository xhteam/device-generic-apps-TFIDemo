package com.quester.demo.infrared;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.content.Context;
import android.hardware.SerialManager;
import android.hardware.SerialPort;
import android.os.SystemProperties;
import android.util.Log;

/**
 * Serial port transmit
 * @author John.Jian
 */
public class SerialComm {
	
	private final String TAG = "infrared";
	private final String SERIAL_PATH = "/dev/ttyUSB1";
	private final int BUF_LENGTH = 256;
	
	private SerialManager mSerialManager;
	private SerialPort mSerialPort;
	private ByteBuffer mBuffer;
	private String mSerialPath;
	
	public SerialComm(Context context) {
		mSerialManager = (SerialManager)context.getSystemService(Context.SERIAL_SERVICE);
		mBuffer = ByteBuffer.allocateDirect(BUF_LENGTH);
		mSerialPath = SystemProperties.get("ro.infrared.port", SERIAL_PATH);
	}
	
	public boolean openSerial(int bandrate) {
		boolean flag = false;
		try {
			mSerialPort = mSerialManager.openSerialPort(mSerialPath, bandrate);
			flag = true;
			Log.i(TAG, "open serial port " + mSerialPath + " (" + bandrate + ")");
		} catch (IOException e) {
			mSerialPort = null;
			Log.e(TAG, "openSerial, " + e.getMessage());
		}
		return flag;
	}
	
	public void closeSerial() {
		if (mSerialPort != null) {
			try {
				mSerialPort.close();
				mSerialPort = null;
				Log.i(TAG, "close serial port " + mSerialPath);
			} catch (IOException e) {
				Log.e(TAG, "closeSerial, " + e.getMessage());
			}
		}
	}

	public byte[] readSerial() {
		byte[] data = null;
		if (mSerialPort != null) {
			try {
				mBuffer.clear();
				int ret = mSerialPort.read(mBuffer);
				if (ret > 0) {
					mBuffer.rewind();
					data = new byte[ret];
					System.arraycopy(mBuffer.array(), 0, data, 0, ret);
				}
			} catch (IOException e) {
				Log.e(TAG, "readSerial, " + e.getMessage());
			}
		}
		return data;
	}
	
	public void writeSerial(byte[] data) {
		try {
			mBuffer.clear();
			mBuffer.put(data);
			mBuffer.flip();
			mSerialPort.write(mBuffer, mBuffer.limit());
		} catch (IOException e) {
			Log.e(TAG, "writeSerial, " + e.getMessage());
		}
	}
	
}
