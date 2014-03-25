package fr.medes.android.maps.app;

import org.osmdroid.util.GeoPoint;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

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
		getSupportMenuInflater().inflate(R.menu.maps__menu_viewer, menu);
		return super.onCreateOptionsMenu(menu);
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
