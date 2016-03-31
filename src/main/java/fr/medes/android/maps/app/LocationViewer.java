package fr.medes.android.maps.app;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.overlay.BubbleOverlay;

public class LocationViewer extends MapActivity implements MapFragment.Callback {

	private GeoPoint point;

	@Override
	public void onViewCreated(MapView view) {
		final Intent intent = getIntent();
		double lat = intent.getDoubleExtra(MapsConstants.EXTRA_LATITUDE, MapsConstants.DEFAULT_LATIUDE);
		double lon = intent.getDoubleExtra(MapsConstants.EXTRA_LONGITUDE, MapsConstants.DEFAULT_LONGITUDE);

		view.getOverlays().add(new BubbleOverlay(this, point = new GeoPoint(lat, lon)));
		mMapFragment.autozoom(point);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.maps__viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.maps__menu_autozoom) {
			mMapFragment.autozoom(point);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
