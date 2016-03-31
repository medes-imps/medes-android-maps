package fr.medes.android.maps.app;

import android.content.Intent;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.overlay.BubbleOverlay;

public class LocationViewer extends MapActivity implements MapFragment.Callback {

	@Override
	public void onViewCreated(MapView view) {
		final Intent intent = getIntent();
		double lat = intent.getDoubleExtra(MapsConstants.EXTRA_LATITUDE, MapsConstants.DEFAULT_LATIUDE);
		double lon = intent.getDoubleExtra(MapsConstants.EXTRA_LONGITUDE, MapsConstants.DEFAULT_LONGITUDE);

		BubbleOverlay overlay = new BubbleOverlay(this, new GeoPoint(lat, lon));

		view.getOverlays().add(overlay);
	}
}
