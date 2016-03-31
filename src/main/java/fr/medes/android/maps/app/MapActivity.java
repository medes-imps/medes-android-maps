package fr.medes.android.maps.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.constants.GeoConstants;

import fr.medes.android.app.SingleChoiceDialogFragment;
import fr.medes.android.app.WakefulIntentService;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.offline.TileLoaderActivity;
import fr.medes.android.maps.offline.TileLoaderService;

public class MapActivity extends AppCompatActivity implements SingleChoiceDialogFragment.Callback,
		TileLoaderDialogFragment.Callback {

	MapFragment mMapFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
		if (mMapFragment == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, mMapFragment = new MapFragment())
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCheckedItemSet(int checkedItem, String tag) {
		mMapFragment.getMapView().setTileSource(TileSourceFactory.getTileSource(checkedItem));
	}

	@Override
	public void onSettingsSet(String tileSourceName, double latitude, double longitude, double distance, int zoomMin, int zoomMax) {
		final double lat = Math.toRadians(latitude);
		final double deltaLat = distance / (2 * GeoConstants.RADIUS_EARTH_METERS);
		final double deltaLon = Math.abs(2 * Math.asin(Math.sin(distance / (4 * GeoConstants.RADIUS_EARTH_METERS)) / Math.cos(lat)));

		final double deltaLatDegree = Math.toDegrees(deltaLat);
		final double deltaLonDegree = Math.toDegrees(deltaLon);

		final double latNorth = latitude + deltaLatDegree;
		final double latSouth = latitude - deltaLatDegree;
		final double lonEast = longitude + deltaLonDegree;
		final double lonWest = longitude - deltaLonDegree;

		Intent intent = new Intent(this, TileLoaderService.class);
		intent.putExtra(MapsConstants.EXTRA_TILE_SOURCE, tileSourceName);
		intent.putExtra(MapsConstants.EXTRA_LAT_NORTH, latNorth);
		intent.putExtra(MapsConstants.EXTRA_LAT_SOUTH, latSouth);
		intent.putExtra(MapsConstants.EXTRA_LON_EAST, lonEast);
		intent.putExtra(MapsConstants.EXTRA_LON_WEST, lonWest);
		intent.putExtra(MapsConstants.EXTRA_ZOOM_MIN, zoomMin);
		intent.putExtra(MapsConstants.EXTRA_ZOOM_MAX, zoomMax);

		WakefulIntentService.sendWakefulWork(this, intent);

		startActivity(new Intent(this, TileLoaderActivity.class));

		finish();
	}

}
