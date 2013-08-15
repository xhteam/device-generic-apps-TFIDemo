package com.quester.demo;

import com.quester.demo.barcode.BarcodeActivity;
import com.quester.demo.scard.SCardActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ImageButton barcode = (ImageButton)findViewById(R.id.goto_barcode);
		barcode.setOnClickListener(this);
		TextView dbarcode = (TextView)findViewById(R.id.goto_barcode_either);
		dbarcode.setOnClickListener(this);
		
		ImageButton scard = (ImageButton)findViewById(R.id.goto_scard);
		scard.setOnClickListener(this);
		TextView dscard = (TextView)findViewById(R.id.goto_scard_either);
		dscard.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.goto_barcode || id == R.id.goto_barcode_either) {
			Intent mIntent = new Intent(MainActivity.this, BarcodeActivity.class);
			startActivity(mIntent);
		} else if (id == R.id.goto_scard || id == R.id.goto_scard_either) {
			Intent mIntent = new Intent(MainActivity.this, SCardActivity.class);
			startActivity(mIntent);
		}
	}

}
