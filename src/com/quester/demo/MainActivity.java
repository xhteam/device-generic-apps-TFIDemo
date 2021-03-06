package com.quester.demo;

import java.io.File;

import com.quester.demo.barcode.BarcodeActivity;
import com.quester.demo.barcode.BarcodeService;
import com.quester.demo.barcode.NewBarcodeActivity;
import com.quester.demo.barcode.Status;
import com.quester.demo.gps.GpsActivity;
import com.quester.demo.headset.HeadsetSettings;
import com.quester.demo.infrared.InfraredActivity;
import com.quester.demo.nfc.NfcActivity;
import com.quester.demo.scard.SCardActivity;
import com.quester.demo.sensor.SensorActivity;
import com.quester.demo.tp.TouchpanelActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Small Launcher
 * @author John.Jian
 */
public class MainActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "barcode";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//wangxi
		Intent startService = new Intent(this, BarcodeService.class);
		startService(startService);
		
		ImageButton barcode = (ImageButton)findViewById(R.id.goto_barcode);
		barcode.setOnClickListener(this);
		TextView dbarcode = (TextView)findViewById(R.id.goto_barcode_either);
		dbarcode.setOnClickListener(this);
		
		ImageButton nfc = (ImageButton)findViewById(R.id.goto_nfc);
		nfc.setOnClickListener(this);
		TextView dnfc = (TextView)findViewById(R.id.goto_nfc_either);
		dnfc.setOnClickListener(this);
		
		ImageButton infrared = (ImageButton)findViewById(R.id.goto_infrared);
		infrared.setOnClickListener(this);
		TextView dinfrared = (TextView)findViewById(R.id.goto_infrared_either);
		dinfrared.setOnClickListener(this);
		
		ImageButton scard = (ImageButton)findViewById(R.id.goto_scard);
		scard.setOnClickListener(this);
		TextView dscard = (TextView)findViewById(R.id.goto_scard_either);
		dscard.setOnClickListener(this);
		
		ImageButton gps = (ImageButton)findViewById(R.id.goto_gps);
		gps.setOnClickListener(this);
		TextView dgps = (TextView)findViewById(R.id.goto_gps_either);
		dgps.setOnClickListener(this);
		
		ImageButton sensor = (ImageButton)findViewById(R.id.goto_sensor);
		sensor.setOnClickListener(this);
		TextView dsensor = (TextView)findViewById(R.id.goto_sensor_either);
		dsensor.setOnClickListener(this);
		
		ImageButton touch = (ImageButton)findViewById(R.id.goto_tp);
		touch.setOnClickListener(this);
		TextView dtouch = (TextView)findViewById(R.id.goto_tp_either);
		dtouch.setOnClickListener(this);
		
		ImageButton headset = (ImageButton)findViewById(R.id.goto_headset);
		headset.setOnClickListener(this);
		TextView dheadset = (TextView)findViewById(R.id.goto_headset_either);
		dheadset.setOnClickListener(this);
	}

	private boolean checkVersion()
	{
		File file = new File(NewBarcodeActivity.BARCODE_PATH);
		if (file.exists())
		{
			Log.i(TAG, "new barcode exist");
			return true;
		}
		else
		{
			Log.i(TAG, "new barcode not exist");
			return false;
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.goto_barcode || id == R.id.goto_barcode_either) {
			if (checkVersion())
			{
				Intent i = new Intent(Status.ACTION_NEW_TRIGGER_START);
				startActivity(i);
			}
			else
			{
				Intent mIntent = new Intent(MainActivity.this, BarcodeActivity.class);
				startActivity(mIntent);
			}

		} else if (id == R.id.goto_nfc || id == R.id.goto_nfc_either) {
			Intent mIntent = new Intent(MainActivity.this, NfcActivity.class);
			startActivity(mIntent);
		} else if (id == R.id.goto_infrared || id == R.id.goto_infrared_either) {
			Intent mIntent = new Intent(MainActivity.this, InfraredActivity.class);
			startActivity(mIntent);
		} else if (id == R.id.goto_scard || id == R.id.goto_scard_either) {
			Intent mIntent = new Intent(MainActivity.this, SCardActivity.class);
			startActivity(mIntent);
		} else if (id == R.id.goto_gps || id == R.id.goto_gps_either) {
			Intent mIntent = new Intent(MainActivity.this, GpsActivity.class);
			startActivity(mIntent);
		} else if (id == R.id.goto_sensor || id == R.id.goto_sensor_either) {
			Intent mIntent = new Intent(MainActivity.this, SensorActivity.class);
			startActivity(mIntent);
		} else if (id == R.id.goto_tp || id == R.id.goto_tp_either) {
			Intent mIntent = new Intent(MainActivity.this, TouchpanelActivity.class);
			startActivity(mIntent);
		} else if (id == R.id.goto_headset || id == R.id.goto_headset_either) {
			Intent mIntent = new Intent(MainActivity.this, HeadsetSettings.class);
			startActivity(mIntent);
		}
	}

}
