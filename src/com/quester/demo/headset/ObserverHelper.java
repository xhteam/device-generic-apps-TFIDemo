package com.quester.demo.headset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ObserverHelper extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent jumper = new Intent(context, HeadsetObserver.class);
		context.startService(jumper);
	}

}
