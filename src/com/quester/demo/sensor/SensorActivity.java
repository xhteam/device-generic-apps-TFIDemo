package com.quester.demo.sensor;

import java.util.List;

import com.quester.demo.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Foreground activity that shows sensor data
 * @author John.Jian
 */
public class SensorActivity extends Activity implements SensorEventListener {
	
	private SensorManager mSensorManager;
	private List<Sensor> mAccelSensors;
	private List<Sensor> mMagneticSensors;
	private Sensor mAccel;
	private Sensor mMagnetic;
	private boolean mAccelEnable;
	private boolean mMagneticEnable;
	private TextView mAccelX, mAccelY, mAccelZ;
	private TextView mMagneticX, mMagneticY, mMagneticZ;
	private String mAxisX, mAxisY, mAxisZ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mAccelSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (!mAccelSensors.isEmpty()) {
			mAccelEnable = true;
			mAccel = mAccelSensors.get(0);
		} else {
			mAccelEnable = false;
		}
		mMagneticSensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if (!mMagneticSensors.isEmpty()) {
			mMagneticEnable = true;
			mMagnetic = mMagneticSensors.get(0);
		} else {
			mMagneticEnable = false;
		}
		
		mAccelX = (TextView)findViewById(R.id.sensor_accel_x);
		mAccelY = (TextView)findViewById(R.id.sensor_accel_y);
		mAccelZ = (TextView)findViewById(R.id.sensor_accel_z);
		mMagneticX = (TextView)findViewById(R.id.sensor_magnetic_x);
		mMagneticY = (TextView)findViewById(R.id.sensor_magnetic_y);
		mMagneticZ = (TextView)findViewById(R.id.sensor_magnetic_z);
		
		mAxisX = getString(R.string.sensor_x);
		mAxisY = getString(R.string.sensor_y);
		mAxisZ = getString(R.string.sensor_z);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mAccelEnable) {
			mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (mMagneticEnable) {
			mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mAccelEnable || mMagneticEnable) {
			mSensorManager.unregisterListener(this);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (mAccelEnable && event.sensor == mAccel) {
			mAccelX.setText(mAxisX + event.values[0]);
			mAccelY.setText(mAxisY + event.values[1]);
			mAccelZ.setText(mAxisZ + event.values[2]);
		} else if (mMagneticEnable && event.sensor == mMagnetic) {
			mMagneticX.setText(mAxisX + event.values[0]);
			mMagneticY.setText(mAxisY + event.values[1]);
			mMagneticZ.setText(mAxisZ + event.values[2]);
		}
	}

}
