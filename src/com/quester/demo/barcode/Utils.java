package com.quester.demo.barcode;

import java.util.Locale;

import android.util.Log;

public class Utils {
	
	public static final String TAG = "barcode";
	public static final int BUF_LENGTH = 192;
	
	public static final int MODE_TRIGGER = 0;
	public static final int MODE_SENSOR = 1;
	public static final int MODE_CONTINUE = 2;
	
	public static String success;
	public static String failture;
	public static String unknown;

	public static void log(String name, byte[] buffer) {
		int lens = buffer.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lens; i++) {
			String hex = Integer.toHexString(buffer[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			sb.append(hex.toUpperCase(Locale.getDefault()) + " ");
		}
		Log.i(TAG, name + ", " + sb.toString());
	}
	
	public static void delay(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			Log.e(TAG, "Delay failed");
		}
	}
	
}
