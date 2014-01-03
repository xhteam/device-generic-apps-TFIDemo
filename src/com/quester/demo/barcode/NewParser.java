package com.quester.demo.barcode;

import android.util.Log;

public class NewParser implements NewCommand
{
	private static final String TAG = "NewParser";
	
	public static byte[] getCommand(byte[] data) 
	{
		//format : PREFIX + COMMAND
		int lens = PREFIX.length + data.length;
		byte[] cmd = new byte[lens];
		
		System.arraycopy(PREFIX, 0, cmd, 0, PREFIX.length);
		System.arraycopy(data, 0, cmd, PREFIX.length, data.length);
		
//		Log.i(TAG, "PREFIX : " + new String(PREFIX));	//wangxi
//		Log.i(TAG, "data : " + new String(data));
		Log.i(TAG, "cmd : " + new String(cmd));
		return cmd;
	}
}
