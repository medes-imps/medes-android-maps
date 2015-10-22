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

package fr.medes.android.radar.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.radar.view.RadarView;

/**
 * Simple Activity wrapper that hosts a {@link RadarView}
 */
public class RadarActivity extends AppCompatActivity {

	private static final int LOCATION_UPDATE_INTERVAL_MILLIS = 1000;

	private static final String RADAR = "radar";

	private static final String PREF_METRIC = "metric";

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

		// Register for location updates
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL_MILLIS, 1, mRadar);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL_MILLIS, 1, mRadar);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mRadar);
		mLocationManager.removeUpdates(mRadar);

		// Stop animating the radar screen
		mRadar.stopSweep();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.maps__menu_radar, menu);
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
		e.commit();
		mRadar.setUseMetric(useMetric);
	}
}
