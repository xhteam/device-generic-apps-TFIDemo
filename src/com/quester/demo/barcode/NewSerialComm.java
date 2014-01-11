package com.quester.demo.barcode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import android.content.Context;
import android.hardware.SerialManager;
import android.hardware.SerialPort;
import android.os.SystemProperties;
import android.util.Log;

public class NewSerialComm {
	private final String TAG = "barcode";
	
	private final static String SERIAL_PATH = "/dev/ttymxc1";
	private final int BAUDRATE = 9600;
	private final int BUFFER_LENGTH = 192;
	
	private SerialManager mSerialManager;
	private SerialPort mSerialPort;
	private ByteBuffer mBuffer;
	private String mSerialPath;
	
	public NewSerialComm(Context context) {
		mSerialManager = (SerialManager)context.getSystemService(Context.SERIAL_SERVICE);
		mBuffer = ByteBuffer.allocateDirect(BUFFER_LENGTH);
		mSerialPath = SystemProperties.get("ro.barcode.port", SERIAL_PATH);
	}
	
	public boolean turnOnPower()
	{
		Log.i(TAG, "turn on power");
		try {
			FileOutputStream os = new FileOutputStream(NewBarcodeActivity.BARCODE_PATH);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write("power on ");
			osw.flush();
			osw.close();
			os.close();
			Thread.sleep(2000);
			
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
		return false;
	}
		
	public boolean turnOffPower()
	{
		Log.i(TAG, "turn off power");
		try {
			FileOutputStream os = new FileOutputStream(NewBarcodeActivity.BARCODE_PATH);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write("power off ");
			osw.flush();
			osw.close();
			os.close();
			
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	public boolean ifPowerOn()
	{
		try {
			char[] buffer = new char[1024]; 
			FileInputStream is = new FileInputStream(NewBarcodeActivity.BARCODE_PATH);
			InputStreamReader isr = new InputStreamReader(is);
			isr.read(buffer);
			isr.close();
			is.close();
			if (buffer != null)
			{
				String tmp = new String(buffer);
				
				String[] result = tmp.split("\n");
				int j;
				for(j = 0 ; j<result.length ; j++)
				{
					if (result[j].contains("power["))
					{
						tmp = result[j];
						Log.i(TAG, "line : " + j + " : " + tmp);	//wangxi
						break;
					}
				}
				if (j == result.length)
				{
					Log.e(TAG, "unknown message");
					return false;
				}
				
				result = tmp.split("\\[");
				tmp = result[1];
				tmp = tmp.substring(0, 3);
				if (tmp.equals("low"))
				{
					return true;
				}
				else if (tmp.equals("hig"))
				{
					return false;
				}
				else
				{
					Log.e(TAG, "error state");
				}
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, "file not found");
		} catch (IOException e) {
			e.printStackTrace();
		} 		
		return false;
	}
	
	/* Try to open serial port */
	public boolean openSerial() {
		boolean flag = false;
		try {
			mSerialPort = mSerialManager.openSerialPort(mSerialPath, BAUDRATE);
			flag = true;
			Log.i(TAG, "open serial port : " + mSerialPath);
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
				Log.i(TAG, "close serial port ");
			} catch (IOException e) {
				Log.e(TAG, "closeSerial, " + e.getMessage());
			}
		}
	}
	
//	/* Device connection status */
//	public String[] isConnected() {
//		String[] info = null;
//		if (mSerialPort != null) {
//			writeSerial(NewParser.getCommand(NewParser.FIRMWAER_VERSION_LIST.getBytes()));
//			byte[] reply = readSerial();
//			int len = NewParser.FIRMWAER_VERSION_LIST.length();
//			if (reply != null && reply.length > len)
//			{
//				String str = new String(reply);
//				str = str.substring(reply.length - len);
//				Log.i(TAG, "reply : " + str);	//wangxi
//				if (str.equals("%%%VER" + new String(NewParser.VALID_COMMAND)))
//				{
//					info = getFirmwareVersion(new String(reply));
//				}
//			}
//		}
//		return info;
//	}
//	
//	private String[] getFirmwareVersion(String version)
//	{
//		String[] info = new String[3];
//		if (version == null || version.equals(""))
//		{
//			return null;
//		}
//		String[] array = version.split("\n");
//		if (array != null && array.length >= 3)
//		{
//			info[0] = array[0];
//			info[1] = array[1];
//			info[2] = array[2];
//		}
//		return info;
//	}
	
	/* Reads serial port */
	public byte[] readSerial() {
		byte[] data = null;
		int length = 0;
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
						Thread.sleep(100);
						mBuffer.clear();
						int rets = mSerialPort.read(mBuffer);
						mBuffer.rewind();
						if (rets > 0) {
							mBuffer.rewind();
							data = new byte[ret+rets];
							length = ret + rets;
							System.arraycopy(recvBuf, 0, data, 0, ret);
							System.arraycopy(mBuffer.array(), 0, data, ret, rets);
						} else {
							data = recvBuf;
						}
					}
				}
			} 
			catch (IOException e) {
				Log.e(TAG, "readSerial, " + e.getMessage());
			} 
			catch (InterruptedException e) {
				Log.e(TAG, "readSerial, " + e.getMessage());
			}
			
			Log.i(TAG, "length : " + length);	//wangxi
			if (data != null)
				Log.i(TAG, "data : " + new String(data));	//wangxi
		}
		return data;
	}
	
	/* Writes serial port */
	public void writeSerial(byte[] data) {
		//send a null before every cmd
		byte[] head = {0x00};
		writeSerialPort(NewParser.getCommand(head));
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		writeSerialPort(data);
	}
	
	private void writeSerialPort(byte[] data) {
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
