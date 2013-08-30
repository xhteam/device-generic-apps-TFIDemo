package com.quester.demo.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Start BarcodeService when system boot completed
 * @author John.Jian
 */
public class BarcodeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startService = new Intent(context, BarcodeService.class);
		context.startService(startService);
	}

}
