package fr.medes.android.maps.app;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.overlay.BubbleClickableOverlay;

public class LocationPicker extends MapActivity implements MapFragment.Callback {

	private BubbleClickableOverlay mOverlay;

	@Override
	public void onViewCreated(MapView view) {
		mMapFragment.setPrecachingEnabled(false);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location l = null;
		try {
			l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (l == null) {
				l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		final double defaulLat = l != null ? l.getLatitude() : MapsConstants.DEFAULT_LATIUDE;
		final double defaultLon = l != null ? l.getLongitude() : MapsConstants.DEFAULT_LONGITUDE;

		final GeoPoint point;
		if (getIntent().hasExtra(MapsConstants.EXTRA_LATITUDE)
				&& getIntent().hasExtra(MapsConstants.EXTRA_LONGITUDE)) {
			final Intent intent = getIntent();
			double lat = intent.getDoubleExtra(MapsConstants.EXTRA_LATITUDE, defaulLat);
			double lon = intent.getDoubleExtra(MapsConstants.EXTRA_LONGITUDE, defaultLon);
			point = new GeoPoint(lat, lon);
		} else {
			point = new GeoPoint(defaulLat, defaultLon);
		}

		mOverlay = new BubbleClickableOverlay(this, point);
		view.getOverlays().add(mOverlay);

		mMapFragment.autozoom(point);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		IGeoPoint point = mOverlay.getGeoPoint();
		outState.putInt(MapsConstants.EXTRA_LATITUDE, point.getLatitudeE6());
		outState.putInt(MapsConstants.EXTRA_LONGITUDE, point.getLongitudeE6());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int latitude = savedInstanceState.getInt(MapsConstants.EXTRA_LATITUDE);
		int longitude = savedInstanceState.getInt(MapsConstants.EXTRA_LONGITUDE);
		GeoPoint point = new GeoPoint(latitude, longitude);
		mOverlay.setGeoPoint(point);

		mMapFragment.autozoom(point);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.maps__picker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.maps__menu_accept) {
			IGeoPoint point = mOverlay.getGeoPoint();
			Location location = new Location("medesmapprovider");
			location.setLatitude(point.getLatitudeE6() / 1E6);
			location.setLongitude(point.getLongitudeE6() / 1E6);
			setResult(RESULT_OK, new Intent().putExtra(MapsConstants.EXTRA_LOCATION, location));
			finish();
			return true;
		} else if (id == R.id.maps__menu_autozoom) {
			mMapFragment.autozoom(mOverlay.getGeoPoint());
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
