package com.quester.demo.barcode;

/**
 * Parses the serial port data
 * @author John.Jian
 */
public class Parser implements Command {
	
	public static String getDevVersion(byte[] buffer) {
		byte[] data = getResponseQueryData(buffer);
		return (data == null) ? null : new String(data);
	}
	
	public static String getDevDate(byte[] buffer) {
		String devDate = null;
		byte[] data = getResponseQueryData(buffer);
		int dataLens = (data == null) ? 0 : data.length;
		//0x30 + 0x34 + Date length (2bytes) + Date
		if (dataLens > 4 && data[0]==0x30 && data[1]==0x34) {
			byte[] dateBuf = new byte[dataLens-4];
			System.arraycopy(data, 4, dateBuf, 0, dataLens-4);
			devDate = new String(dateBuf);
		}
		return devDate;
	}
	
	public static String getDevSn(byte[] buffer) {
		String devSn = null;
		byte[] data = getResponseQueryData(buffer);
		int dataLens = (data == null) ? 0 : data.length;
		//0x30 + 0x33 + S/N length (2bytes) + S/N
		if (dataLens > 4 && data[0]==0x30 && data[1]==0x33) {
			byte[] snBuf = new byte[dataLens-4];
			System.arraycopy(data, 4, snBuf, 0, dataLens-4);
			devSn = new String(snBuf);
		}
		return devSn;
	}
	
	public static String getDevEsn(byte[] buffer) {
		String devEsn = null;
		byte[] data = getResponseQueryData(buffer);
		int dataLens = (data == null) ? 0 : data.length;
		//0x30 + 0x32 + ESN length (2bytes) + ESN
		if (dataLens > 4 && data[0]==0x30 && data[1]==0x32) {
			byte[] esnBuf = new byte[dataLens-4];
			System.arraycopy(data, 4, esnBuf, 0, dataLens-4);
			devEsn = new String(esnBuf);
		}
		return devEsn;
	}
	
	public static int getReadMode(byte[] buffer) {
		int mode = -1;
		byte[] data = getResponseQueryData(buffer);
		int dataLens = (data == null) ? 0 : data.length;
		if (dataLens == 3) {
			if (data[0] == 0x30 && data[1] == 0x30) {
				if (data[2] == 0x30) {
					mode = 0x30;
				} else if (data[2] == 0x31) {
					mode = 0x31;
				} else if (data[2] == 0x32) {
					mode = 0x32;
				}
			}
		}
		return mode;
	}
	
	/**
	 * Generate complete setting command
	 * @param data general setting attributes
	 * @return complete setting command
	 */
	public static byte[] getSettingCommand(String data) {
		// Setting format: {PREFIX_LOWER/PREFIX_UPPER}{DATA}[=NUM/HEX/"STR"]
		return (PREFIX_UPPER + data).getBytes();
	}
	
	/**
	 * Generate complete query command
	 * @param data general query attributes
	 * @return complete query command
	 */
	public static byte[] getQueryCommand(byte[] data) {
		// Query format: {PREFIX_SEND}{DATA_LENS}{TYPES}{DATA}{LRC}
		// Length:             2           2        1    lens   1
		int lens = data.length + 1;
		byte len0 = (byte)(lens>>8);
		byte len1 = (byte)lens;
		byte[] queryCmd = new byte[2+2+1+lens-1+1];
		
		// PERFIX_SEND -> queryCmd[0], queryCmd[1]
		System.arraycopy(PREFIX_SEND, 0, queryCmd, 0, 2);
		// DATA_LENS
		queryCmd[2] = len0;
		queryCmd[3] = len1;
		// TYPES
		queryCmd[4] = QUERY_TYPES;
		// DATA
		System.arraycopy(data, 0, queryCmd, 5, data.length);
		// LRC
		queryCmd[queryCmd.length-1] = getLRC(len1, QUERY_TYPES, data, data.length);
		
		println("queryCmd", queryCmd);
		return queryCmd;
	}
	
	private static void println(String tag, byte[] buffer) {
		int lens = buffer.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lens; i++) {
			sb.append(String.format("%02X ", buffer[i]));
		}
		System.out.println(tag + " -> " + sb.toString());
	}
	
	/**
	 * Get query or receive data LRC value
	 * @param len lower byte of data lens
	 * @param type data types
	 * @param data stream
	 * @return LRC value
	 */
	private static byte getLRC(byte lowLen, byte type, byte[] data, int dataLens) {
		// LRC = 0xff^len^type^data
		byte result = (byte)(0xff^lowLen^type);
		for (int i = 0; i < dataLens; i++) {
			result = (byte)(result^data[i]);
		}
		return result;
	}
	
	/*
	 * Validity check for response data by query command
	 * Receive format: {PREFIX_RECV}{DATA_LENS}{TYPES}{DATA}{LRC}
	 * Length:               2           2        1    lens   1
	 */
	private static byte[] getResponseQueryData(byte[] buffer) {
		int lens = buffer.length;
		if (lens < 7) {
			return null;
		}
		
		if ((buffer[0]!=PREFIX_RECV[0]) || (buffer[1]!=PREFIX_RECV[1]) 
				|| (buffer[4]!=RECV_TYPES)) {
			return null;
		}
		
		int dataLens = ((buffer[2]&0xff)<<8) + (buffer[3]&0xff) - 1;
		byte[] data = new byte[dataLens];
		System.arraycopy(buffer, 5, data, 0, dataLens);
		
		byte lrc = getLRC(buffer[3], RECV_TYPES, data, dataLens);
		if ((lrc&0xff) != (buffer[lens-1]&0xff)) {
			return null;
		}
		
		return data;
	}
	
}
