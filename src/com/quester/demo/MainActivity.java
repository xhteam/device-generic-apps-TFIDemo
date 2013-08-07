package com.quester.demo;

import com.quester.demo.barcode.BarcodeActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button barcode = (Button)findViewById(R.id.barcode);
		barcode.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, BarcodeActivity.class);
				startActivity(mIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
