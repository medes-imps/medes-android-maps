/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.medes.android.maps.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.view.RadarView;

/**
 * Simple Activity wrapper that hosts a {@link RadarView}
 */
public class RadarActivity extends AppCompatActivity {

	private static final int LOCATION_UPDATE_INTERVAL_MILLIS = 1000;

	private static final String RADAR = "radar";
	private static final String PREF_METRIC = "metric";

	private static final int REQUEST_PERMISSION = 1;

	private SensorManager mSensorManager;

	private Sensor mSensor;

	private RadarView mRadar;

	private LocationManager mLocationManager;

	private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maps__radar_content);
		mRadar = (RadarView) findViewById(R.id.maps__radar);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// Metric or standard units?
		mPrefs = getSharedPreferences(RADAR, MODE_PRIVATE);
		boolean useMetric = mPrefs.getBoolean(PREF_METRIC, false);
		mRadar.setUseMetric(useMetric);

		// Read the target from our intent
		Intent i = getIntent();
		int latE6 = (int) (i.getDoubleExtra(MapsConstants.EXTRA_LATITUDE, 0) * 1E6);
		int lonE6 = (int) (i.getDoubleExtra(MapsConstants.EXTRA_LONGITUDE, 0) * 1E6);
		mRadar.setTarget(latE6, lonE6);
		mRadar.setDistanceView((TextView) findViewById(R.id.maps__distance));
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mRadar, mSensor, SensorManager.SENSOR_DELAY_GAME);

		// Start animating the radar screen
		mRadar.startSweep();

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			// Register for location updates
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL_MILLIS, 1, mRadar);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL_MILLIS, 1, mRadar);
		} else {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mRadar);

		try {
			mLocationManager.removeUpdates(mRadar);
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		// Stop animating the radar screen
		mRadar.stopSweep();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_PERMISSION:
				for (int grant : grantResults) {
					if (grant != PackageManager.PERMISSION_GRANTED) {
						Toast.makeText(this, R.string.aml__permission_not_granted, Toast.LENGTH_LONG).show();
						finish();
						return;
					}
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.maps__radar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.maps__menu_metric) {
			setUseMetric(false);
			return true;
		} else if (id == R.id.maps__menu_standard) {
			setUseMetric(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUseMetric(boolean useMetric) {
		SharedPreferences.Editor e = mPrefs.edit();
		e.putBoolean(PREF_METRIC, useMetric);
		e.apply();
		mRadar.setUseMetric(useMetric);
	}
}
