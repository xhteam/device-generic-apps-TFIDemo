package com.quester.demo.gps;

import java.util.Date;
import java.util.Iterator;

import com.quester.demo.R;

import android.app.Activity;
import android.location.GpsSatellite;
//import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

/**
 * Foreground activity that shows information about GPS engine 
 * @author John.Jian
 */
public class GpsActivity extends Activity {
	
	private LocationManager mLocationManager;
	private TextView mLocationField, mNmeaField, mAvailSatellite, mUseSatellite;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Settings.Secure.setLocationProviderEnabled(getContentResolver(),
					LocationManager.GPS_PROVIDER, true);
		}
		
		mLocationField = (TextView)findViewById(R.id.gps_location);
		mNmeaField = (TextView)findViewById(R.id.gps_nmea);
		mAvailSatellite = (TextView)findViewById(R.id.gps_available_satellites);
		mUseSatellite = (TextView)findViewById(R.id.gps_useful_satellites);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getGpsLocation();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mLocationManager != null) {
			mLocationManager.removeNmeaListener(mNmeaListener);
			mLocationManager.removeGpsStatusListener(mStatusListener);
			mLocationManager.removeUpdates(mLocListener);
		}
	}
	
	private void getGpsLocation() {
		// Selecting a location provider by criteria.
		// Providers maybe ordered according to accuracy, power usage, ability 
		// to report altitude, speed, and bearing, and monetary cost.
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(true);
//		criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
//		criteria.setSpeedRequired(false);
//		criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
//		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		
		//chose gps provider directly
//		String provider = mLocationManager.getBestProvider(criteria, true);
		String provider = LocationManager.GPS_PROVIDER;
		Location location = mLocationManager.getLastKnownLocation(provider);
		if (location != null) {
			updateToNewLocation(location);
		}
//		if (location == null) {
//			location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		}
		mLocationManager.addNmeaListener(mNmeaListener);
		mLocationManager.addGpsStatusListener(mStatusListener);
		//update per second and do not affected by the distance
		mLocationManager.requestLocationUpdates(provider, 1000, 0, mLocListener);
	}
	
	private final GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener() {
		public void onNmeaReceived(long timestamp, String nmea) {
			// Receive NMEA data from the GPS engine
			updateNmeaSentences(timestamp, nmea);
		}
	};
	
	private final GpsStatus.Listener mStatusListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			// Called to report changes in the GPS status
			// The event number is one of:
			// GPS_EVENT_STARTED, GPS_EVENT_STOPED, GPS_EVENT_FIRST_FIX, GPS_EVENT_SATELLITE_STATUS
			GpsStatus status = mLocationManager.getGpsStatus(null);
			updateGpsStatus(event, status);
		}
	};

	private final LocationListener mLocListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// Called when the location has changed
			updateToNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			// Called when the provider is disabled by the user
		}

		public void onProviderEnabled(String provider) {
			// Called when the provider is enabled by the user
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Called when the provider status changed
			// This method is called when a provider is unable to fetch a location or if 
			// the provider has recently become available after a period of unavailability
			// status: OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE, AVAILABLE
		}
	};
	
	private void updateNmeaSentences(final long timestamp, final String nmea) {
		runOnUiThread(new Runnable() {
			public void run() {
				mNmeaField.append(getNewLine(timestamp + ", " + nmea));
			}
		});
	}
	
	private void updateGpsStatus(final int event, final GpsStatus status) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
					int satelliteCount = status.getMaxSatellites();
					mAvailSatellite.setText(String.valueOf(satelliteCount));
					
					Iterator<GpsSatellite> it = status.getSatellites().iterator();
					int inUse = 0;
					while (it.hasNext()) {
						GpsSatellite gs = it.next();
						// The signal to noise ratio for the satellite
						//gs.getSnr();
						if (gs.usedInFix()) {
							inUse++;
						}
					}
					mUseSatellite.setText(String.valueOf(inUse));
				}
			}
		});
	}
	
	private void updateToNewLocation(final Location location) {
		runOnUiThread(new Runnable() {
			public void run() {
				StringBuilder sb = new StringBuilder();
				sb.append(getNewLine("Device real time: " + new Date().toString()));
				if (location != null) {
					String longitude = "longitude: " + location.getLongitude();
					String latitude = ", latitude: " + location.getLatitude();
					sb.append(getNewLine(longitude + latitude));
					sb.append(getNewLine("UTC time: " + new Date(location.getTime()).toString()));
					
					if (location.hasAccuracy()) {
						sb.append(getNewLine("accuracy: " + location.getAccuracy() + " meters"));
					}
					if (location.hasAltitude()) {
						sb.append(getNewLine("altitude: " + location.getAltitude() + " meters"));
					}
					if (location.hasBearing()) {
						sb.append(getNewLine("bearing: " + location.getBearing() + " degrees"));
					}
					if (location.hasSpeed()) {
						sb.append(getNewLine("speed: " + location.getSpeed() + " meters/second"));
					}
				} else {
					sb.append(getNewLine("invalid location, reloading.."));
				}
				sb.append(getNewLine(""));
				mLocationField.append(sb.toString());
			}
		});
	}
	
	private String getNewLine(String str) {
		return (str + "\n");
	}
	
}
