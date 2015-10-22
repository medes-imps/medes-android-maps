package fr.medes.android.maps.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.osmdroid.util.GeoPoint;

import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.overlay.BubbleOverlay;

public class LocationViewer extends MapActivity {

	private BubbleOverlay mOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = getIntent();
		double lat = intent.getDoubleExtra(MapsConstants.EXTRA_LATITUDE, MapsConstants.DEFAULT_LATIUDE);
		double lon = intent.getDoubleExtra(MapsConstants.EXTRA_LONGITUDE, MapsConstants.DEFAULT_LONGITUDE);

		mOverlay = new BubbleOverlay(this, new GeoPoint(lat, lon));

		getMapView().getOverlays().add(mOverlay);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.maps__menu_viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.maps__menu_map) {
			showDialog(DIALOG_MAPMODE_ID);
			return true;
		} else if (id == R.id.maps__menu_place) {
			changeMyLocationState();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
