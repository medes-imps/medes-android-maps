package fr.medes.android.maps.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import fr.medes.android.app.AmlApplication;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.database.PreCache;
import fr.medes.android.maps.database.sqlite.PreCacheCursor;
import fr.medes.android.maps.overlay.BoundingBoxOverlay;

public class PreCacheMap extends MapActivity {

	private static final int ZOOM_FOR_PRECACHE = 11;

	private String mTileSource = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getBooleanExtra(MapsConstants.EXTRA_SHOW_PRECACHE, false)) {
			showPreCache(getIntent());
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (intent.getBooleanExtra(MapsConstants.EXTRA_SHOW_PRECACHE, false)) {
			showPreCache(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTileSource != null) {
			mMapView.setTileSource(TileSourceFactory.getTileSource(mTileSource));
			mTileSource = null;
		}
	}

	private void showPreCache(final Intent intent) {
		PreCacheCursor c = (PreCacheCursor) getAmlApplication().getOpenHelper().query(PreCache.Columns.CONTENT_URI);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			BoundingBoxOverlay bbo = new BoundingBoxOverlay(this);
			bbo.setBounds(c.getNorth(), c.getEast(), c.getSouth(), c.getWest());
			mMapView.getOverlayManager().add(bbo);
		}
		c.close();

		mTileSource = intent.getStringExtra(MapsConstants.EXTRA_TILE_SOURCE);

		final double north = intent.getDoubleExtra(MapsConstants.EXTRA_LAT_NORTH, MapsConstants.DEFAULT_NORTH);
		final double east = intent.getDoubleExtra(MapsConstants.EXTRA_LON_EAST, MapsConstants.DEFAULT_EAST);
		final double south = intent.getDoubleExtra(MapsConstants.EXTRA_LAT_SOUTH, MapsConstants.DEFAULT_SOUTH);
		final double west = intent.getDoubleExtra(MapsConstants.EXTRA_LON_WEST, MapsConstants.DEFAULT_WEST);

		final GeoPoint center = new GeoPoint((north + south) / 2, (east + west) / 2);

		mMapView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mMapView.getController().setZoom(ZOOM_FOR_PRECACHE);
				mMapView.getController().animateTo(center);
			}
		}, 500);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.maps__menu_precachemap, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.maps__menu_map) {
			showDialog(DIALOG_MAPMODE_ID);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private AmlApplication getAmlApplication() {
		return (AmlApplication) getApplication();
	}

}