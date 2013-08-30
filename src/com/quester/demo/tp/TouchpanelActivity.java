package com.quester.demo.tp;

import com.quester.demo.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Foreground activity that enable the debug functional with touchpanel
 * @author John.Jian
 */
public class TouchpanelActivity extends Activity {
	
	private ContentResolver mResolver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tp);
		mResolver = getContentResolver();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Settings.System.putInt(mResolver, Settings.System.POINTER_LOCATION, 1);
		Settings.System.putInt(mResolver, Settings.System.SHOW_TOUCHES, 1);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Settings.System.putInt(mResolver, Settings.System.POINTER_LOCATION, 0);
		Settings.System.putInt(mResolver, Settings.System.SHOW_TOUCHES, 0);
	}
	
}
