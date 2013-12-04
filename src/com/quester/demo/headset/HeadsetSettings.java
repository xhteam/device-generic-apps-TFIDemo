package com.quester.demo.headset;

import com.quester.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class HeadsetSettings extends Activity {
	
	private Switch mSwitch;
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_headset);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mEditor = mPreferences.edit();
		
		mSwitch = (Switch)findViewById(R.id.headset_switchWidget);
		if (mPreferences.getBoolean("headset_prompt", true)) {
			mSwitch.setChecked(true);
		} else {
			mSwitch.setChecked(false);
		}
		
		mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mEditor.putBoolean("headset_prompt", true);
				} else {
					mEditor.putBoolean("headset_prompt", false);
				}
				mEditor.commit();
				sendSwitchState(isChecked);
			}
		});
	}
	
	private void sendSwitchState(boolean state) {
		Intent intent = new Intent(HeadsetSettings.this, HeadsetObserver.class);
		if (state) {
			startService(intent);
		} else {
			stopService(intent);
		}
	}

}
