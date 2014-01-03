package com.quester.demo.barcode;

public interface NewCommand {

	//Prefix
	public static final byte[] PREFIX = {0x16, 0x4D, 0x0D};	//<SYN> ‘M’ <CR>
	
	//commands
	public static final byte[] TRIGGER_MODE_HOST = {0x30, 0x34, 0x30, 0x31, 0x44, 0x30, 0x35, 0x2E};		//0401D05.
	public static final byte[] TRIGGER_MODE_CONTINUE = {0x30, 0x34, 0x30, 0x31, 0x44, 0x30, 0x33, 0x2E};	//0401D03.
	
	public static final byte[] START_DECODE = {0x16, 0x54, 0x0D};	//<SYN> ‘T’ <CR>
	public static final byte[] STOP_DECODE = {0x16, 0x55, 0x0D};	//<SYN> ‘U’ <CR>
	
	public static final String FIRMWAER_VERSION_LIST = "%%%VER.";

	//NULL
	public static final byte[] A_NULL = {0x00};

	
	//Parameter index
	public static final byte[] TRIGGER_MODE = {0x30, 0x34, 0x30, 0x31};	//0401
	//0401D03
	
	//Numeral system
	public static final byte[] DECIMAL = {0x44};		//D
	public static final byte[] HEXADECIMAL = {0x48};	//H
	
	//Value
	public static final byte[] HOST = {0x30, 0x35};		//05
	public static final byte[] CONTINUE = {0x30, 0x33};	//03
	
	//Storage
	public static final byte[] NON_VOLATILE = {0x2E};	//.
	public static final byte[] VOLATILE = {0x2E};		//!
	public static final byte[] ASK = {0X3F};			//?
	public static final byte[] PERCENT = {0X25};		//%
	
	//responses
	public static final byte[] VALID_COMMAND = {0x06};		//<ACK>
	public static final byte[] INVALID_COMMAND = {0x05};	//<ENQ>
	public static final byte[] INVALID_VALUE = {0x15};		//<NAK>

}
