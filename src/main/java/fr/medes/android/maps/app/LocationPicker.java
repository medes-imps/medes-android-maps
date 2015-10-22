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

import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.overlay.BubbleClickableOverlay;

public class LocationPicker extends MapActivity {

	private BubbleClickableOverlay mOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (l == null) {
			l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		final GeoPoint point;
		if (l != null) {
			point = new GeoPoint(l.getLatitude(), l.getLongitude());
		} else {
			final Intent intent = getIntent();
			double lat = intent.getDoubleExtra(MapsConstants.EXTRA_LATITUDE, MapsConstants.DEFAULT_LATIUDE);
			double lon = intent.getDoubleExtra(MapsConstants.EXTRA_LONGITUDE, MapsConstants.DEFAULT_LONGITUDE);
			point = new GeoPoint(lat, lon);
		}

		mOverlay = new BubbleClickableOverlay(this, point);
		getMapView().getOverlays().add(mOverlay);

	}

	@Override
	public boolean isPrecachingEnabled() {
		return false;
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

		autozoom(point);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.maps__menu_picker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.maps__menu_accept) {
			IGeoPoint point = mOverlay.getGeoPoint();
			Location location = new Location("medesmapprovider");
			location.setLatitude(point.getLatitudeE6() / (double) 1E6);
			location.setLongitude(point.getLongitudeE6() / (double) 1E6);
			setResult(RESULT_OK, new Intent().putExtra(MapsConstants.EXTRA_LOCATION, location));
			finish();
			return true;
		} else if (id == R.id.maps__menu_map) {
			showDialog(DIALOG_MAPMODE_ID);
			return true;
		} else if (id == R.id.maps__menu_place) {
			changeMyLocationState();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
