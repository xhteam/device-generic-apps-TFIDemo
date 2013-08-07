package com.quester.demo.barcode;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.content.Context;
import android.hardware.SerialManager;
import android.hardware.SerialPort;
import android.os.SystemProperties;
import android.util.Log;

/**
 * Serial port communication
 * @author John.Jian
 */
public class SerialComm {
	
	private final String SERIAL_PATH = "/dev/ttyUSB0";
	private final int BAUDRATE = 9600;
	
	private SerialManager mSerialManager;
	private SerialPort mSerialPort;
	private ByteBuffer mBuff;
	private String mSerialPath;
	
	public SerialComm(Context context) {
		mSerialManager = (SerialManager)context.getSystemService(Context.SERIAL_SERVICE);
		mBuff = ByteBuffer.allocateDirect(Utils.BUF_LENGTH);
		mSerialPath = SystemProperties.get("ro.barcode.port", SERIAL_PATH);
	}
	
	/* Try to open specify serial port */
	public boolean openSerial() {
		boolean flag = false;
		try {
			mSerialPort = mSerialManager.openSerialPort(mSerialPath, BAUDRATE);
			flag = true;
		} catch (IOException e) {
			mSerialPort = null;
			Log.e(Utils.TAG, "Could not open serial port " + mSerialPath);
		}
		return flag;
	}
	
	/* Disconnect the serial port connection status */
	public void closeSerial() {
		if (mSerialPort != null) {
			try {
				mSerialPort.close();
				mSerialPort = null;
			} catch (IOException e) {
				Log.e(Utils.TAG, "Could not close serial port " + mSerialPath);
			}
		}
	}
	
	/* Determine that device connection status */
	public boolean isConnected() {
		boolean flag = false;
		if (mSerialPort != null) {
			writeSerial(new byte[] {Command.DEV_ASK});
			byte[] reply = readSerial();
			if (reply.length == 1 && reply[0] == Command.DEV_REPLY) {
				flag = true;
			}
		}
		return flag;
	}
	
	/* Reads serial data and process the data */
	public byte[] readSerial() {
		byte[] buf = null;
		if (mSerialPort != null) {
			try {
				mBuff.clear();
				Utils.delay(Command.MAX_DELAY);
				int ret = mSerialPort.read(mBuff);
				mBuff.rewind();
				buf = new byte[ret];
				System.arraycopy(mBuff.array(), 0, buf, 0, ret);
			} catch (IOException e) {
				Log.e(Utils.TAG, "Exception to reading serial port " + mSerialPath);
			}
		}
		return buf;
	}
	
	/* Writes data to serial port */
	public void writeSerial(byte[] data) {
		try {
			mBuff.clear();
			mBuff.put(data);
			mBuff.flip();
			mSerialPort.write(mBuff, mBuff.limit());
		} catch (IOException e) {
			Log.e(Utils.TAG, "Exception to writing serial port " + mSerialPath);
		}
	}
	
	/* Enable setup code(default within disable) */
	public boolean enableSetupCode() {
		boolean flag = false;
		writeSerial(Command.getSettingCommand(Command.SETUP_CODE_EN));
		byte[] reply = readSerial();
		if (reply.length == 1 && reply[0] == Command.SUCCESS) {
			flag = true;
		}
		return flag;
	}
	
	/* Disable setup code */
	public boolean disableSetupCode() {
		boolean flag = false;
		writeSerial(Command.getSettingCommand(Command.SETUP_CODE_DIS));
		byte[] reply = readSerial();
		if (reply.length == 1 && reply[0] == Command.SUCCESS) {
			flag = true;
		}
		return flag;
	}
	
	/* Reset factory data */
	public boolean resetFactoryData() {
		boolean flag = false;
		if (enableSetupCode()) {
			writeSerial(Command.getSettingCommand(Command.FACTORY_DF));
			byte[] reply = readSerial();
			if (reply.length == 1 && reply[0] == Command.SUCCESS) {
				flag = true;
			}
			disableSetupCode();
		}
		return flag;
	}
	
	/* Choose read mode */
	public boolean setReadMode(int mode) {
		boolean flag = false;
		if (enableSetupCode()) {
			Log.i(Utils.TAG, "start setting");
			if (mode == Utils.MODE_TRIGGER) {
				writeSerial(Command.getSettingCommand(Command.MODE_TRIGGER));
			} else if (mode == Utils.MODE_SENSOR) {
				writeSerial(Command.getSettingCommand(Command.MODE_SENSER));
			} else if (mode == Utils.MODE_CONTINUE) {
				writeSerial(Command.getSettingCommand(Command.MODE_CONTINUE));
			} else {
				disableSetupCode();
				Log.i(Utils.TAG, "Unknown read mode setting");
				return flag;
			}
			byte[] reply = readSerial();
			if (reply.length == 1 && reply[0] == Command.SUCCESS) {
				flag = true;
			}
			Log.i(Utils.TAG, "finish setting");
			disableSetupCode();
		}
		return flag;
	}
	
	/* Modifies electronic serial number */
	public boolean setEsn(String esn) {
		boolean flag = false;
		if (enableSetupCode()) {
			writeSerial(Command.getSettingCommand(Command.EN_ESN_SET + "=" + esn));
			byte[] reply = readSerial();
			if (reply.length == 1 && reply[0] == Command.SUCCESS) {
				flag = true;
			}
			disableSetupCode();
		}
		return flag;
	}
	
}
