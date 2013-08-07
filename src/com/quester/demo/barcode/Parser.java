package com.quester.demo.barcode;

/**
 * Parse the received serial port data
 * @author John.Jian
 */
public class Parser {
	
	/* End chars enable state */
	public static boolean endChars;
	/* MaxLens = 2 */
	public static int endCharsLens;
	/* Values of end chars */
	public static byte[] endCharsBuf;
	
	public static void getEndChars(byte[] buffer) {
		Utils.log("endChars", buffer);
		byte[] data = checkQueryByteArray(buffer);
		int dataLens = data.length;
		if (dataLens < 2) {
			endChars = false;
			endCharsBuf = null;
		} else {
			if (data[0]==0x01) {
				endChars = true;
			} else {
				endChars = false;
			}
			
			if (data[1]==0x02) {
				endCharsBuf = new byte[2];
				endCharsLens = 2;
				System.arraycopy(data, 2, endCharsBuf, 0, endCharsLens);
			} else if (data[1]==0x01) {
				endCharsBuf = new byte[1];
				endCharsLens = 1;
				System.arraycopy(data, 2, endCharsBuf, 0, endCharsLens);
			} else {
				endCharsBuf = null;
			}
		}
	}
	
	public static String getDevVersion(byte[] buffer) {
		Utils.log("version", buffer);
		return new String(checkQueryByteArray(buffer));
	}
	
	public static String getDevDate(byte[] buffer) {
		Utils.log("date", buffer);
		byte[] data = checkQueryByteArray(buffer);
		int dataLens = data.length;
		if (dataLens < 5) {
			return null;
		}
		if (data[0]!=0x30 || data[1]!=0x34) {
			return null;
		}
		byte[] dateBuf = new byte[dataLens-4];
		System.arraycopy(data, 4, dateBuf, 0, dataLens-4);
		String dateStr = new String(dateBuf);
		return dateStr;
	}
	
	public static String getDevSn(byte[] buffer) {
		Utils.log("sn", buffer);
		byte[] data = checkQueryByteArray(buffer);
		int dataLens = data.length;
		if (dataLens < 5) {
			return null;
		}
		if (data[0]!=0x30 || data[1]!=0x33) {
			return null;
		}
		byte[] snBuf = new byte[dataLens-4];
		System.arraycopy(data, 4, snBuf, 0, dataLens-4);
		String snStr = new String(snBuf);
		return snStr;
	}
	
	public static String getDevEsn(byte[] buffer) {
		Utils.log("esn", buffer);
		byte[] data = checkQueryByteArray(buffer);
		int dataLens = data.length;
		if (dataLens < 5) {
			return null;
		}
		if (data[0]!=0x30 || data[1]!=0x32) {
			return null;
		}
		byte[] esnBuf = new byte[dataLens-4];
		System.arraycopy(data, 4, esnBuf, 0, dataLens-4);
		String esnStr = new String(esnBuf);
		return esnStr;
	}
	
	/*
	 * Validity check for response data by query command
	 * Receive format: {PREFIX_RECV}{DATA_LENS}{TYPES}{DATA}{LRC}
	 * Length:               2           2        1    lens   1
	 */
	private static byte[] checkQueryByteArray(byte[] buffer) {
		int lens = buffer.length;
		if (lens < 7) {
			return null;
		}
		
		if ((buffer[0]!=Command.PREFIX_RECV[0]) || (buffer[1]!=Command.PREFIX_RECV[1]) 
				|| (buffer[4]!=Command.RECV_TYPES)) {
			return null;
		}
		
		int dataLens = ((buffer[2]&0xff)<<8) + (buffer[3]&0xff) - 1;
		byte[] data = new byte[dataLens];
		System.arraycopy(buffer, 5, data, 0, dataLens);
		
		byte lrc = Command.getLRC(buffer[3], Command.RECV_TYPES, data, dataLens);
		if ((lrc&0xff) != (buffer[lens-1]&0xff)) {
			return null;
		}
		
		return data;
	}
}
