package com.quester.demo.barcode;

public class Status {

	public static final String ACTION_TRIGGER = "quester.intent.action.barcode";
	
	//
	public static final String ACTION_NEW_TRIGGER = "quester.intent.action.new_barcode";
	public static final String ACTION_NEW_TRIGGER_START = "quester.intent.action.new_barcode_start";
	public static final String EXTRA_TRIGGER_ONCE = "trigger_once";
	
	//
	public static final String ACTION_NEW_TRIGGER_BROADCAST = "quester.intent.action.new_barcode_broadcast";
	
	public static boolean response = false;
	public static boolean trigging = false;
	public static boolean ready = false;
	
	//
	public static boolean BUTTON_TRIGGER = true; 
	
	public static final int ACTIVITY_ON = 1;
	
	public static final int ACTIVITY_OFF = 0;
	
	public static final int ACTIVITY_PAUSE = 2;
	
	public static int BARCODE_ACTIVITY = ACTIVITY_OFF;
}
