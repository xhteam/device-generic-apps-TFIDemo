package com.quester.demo.barcode;

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
	
	private final String TAG = "barcode";
	private final String SERIAL_PATH = "/dev/ttyUSB0";
	private final int BAUDRATE = 9600;
	private final int BUFFER_LENGTH = 192;
	
	private SerialManager mSerialManager;
	private SerialPort mSerialPort;
	private ByteBuffer mBuffer;
	private String mSerialPath;
	
	public SerialComm(Context context) {
		mSerialManager = (SerialManager)context.getSystemService(Context.SERIAL_SERVICE);
		mBuffer = ByteBuffer.allocateDirect(BUFFER_LENGTH);
		mSerialPath = SystemProperties.get("ro.barcode.port", SERIAL_PATH);
	}
	
	/* Try to open serial port */
	public boolean openSerial() {
		boolean flag = false;
		try {
			mSerialPort = mSerialManager.openSerialPort(mSerialPath, BAUDRATE);
			flag = true;
			Log.i(TAG, "open serial port " + mSerialPath);
		} catch (IOException e) {
			mSerialPort = null;
			Log.e(TAG, "openSerial, " + e.getMessage());
		}
		return flag;
	}
	
	/* Disconnect the serial port */
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
	
	/* Device connection status */
	public boolean isConnected() {
		boolean flag = false;
		if (mSerialPort != null) {
			writeSerial(new byte[] {Command.DEV_ASK});
			byte[] reply = readSerial();
			int lens = (reply == null) ? 0 : reply.length;
			if (lens == 1 && reply[0] == Command.DEV_REPLY) {
				flag = true;
			}
		}
		return flag;
	}
	
	/* Reads serial port */
	public byte[] readSerial() {
		byte[] data = null;
		if (mSerialPort != null) {
			try {
				mBuffer.clear();
				int ret = mSerialPort.read(mBuffer);
				if (ret > 0) {
					mBuffer.rewind();
					byte[] recvBuf = new byte[ret];
					System.arraycopy(mBuffer.array(), 0, recvBuf, 0, ret);
					if (ret == 1 && (recvBuf[0] == Command.SUCCESS 
							|| recvBuf[0] == Command.FAILTURE 
							|| recvBuf[0] == Command.DEV_REPLY)) {
						data = recvBuf;
					} else {
						Thread.sleep(Command.MAX_DELAY);
						mBuffer.clear();
						int rets = mSerialPort.read(mBuffer);
						mBuffer.rewind();
						if (rets > 0) {
							mBuffer.rewind();
							data = new byte[ret+rets];
							System.arraycopy(recvBuf, 0, data, 0, ret);
							System.arraycopy(mBuffer.array(), 0, data, ret, rets);
						} else {
							data = recvBuf;
						}
					}
				}
			} catch (IOException e) {
				Log.e(TAG, "readSerial, " + e.getMessage());
			} catch (InterruptedException e) {
				Log.e(TAG, "readSerial, " + e.getMessage());
			}
		}
		return data;
	}
	
	/* Writes serial port */
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
	
	/* Enable setup code */
	public void enableSetupCode() {
		writeSerial(Parser.getSettingCommand(Command.SETUP_CODE_EN));
	}
	
	/* Disable setup code */
	public void disableSetupCode() {
		writeSerial(Parser.getSettingCommand(Command.SETUP_CODE_DIS));
	}
	
	/* Electronic serial number */
	public void setEsn(String esn) {
		writeSerial(Parser.getSettingCommand(Command.EN_ESN_SET + "=" + esn));
	}
	
	/* Hardware button pressed */
	public void triggerDown() {
		writeSerial(Command.ANALOG_TRIIGER_DOWN);
	}
	
	/* Hardware button released */
	public void triggerUp() {
		writeSerial(Command.ANALOG_TRIGGER_UP);
	}
	
	/* Factory data reset */
	public void resetFactory() {
		writeSerial(Parser.getSettingCommand(Command.FACTORY_DF));
	}
	
}
