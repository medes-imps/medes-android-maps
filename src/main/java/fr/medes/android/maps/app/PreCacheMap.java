package fr.medes.android.maps.app;

import android.content.Intent;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import fr.medes.android.database.sqlite.stmt.QueryBuilder;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.database.PreCache;
import fr.medes.android.maps.database.sqlite.MapsOpenHelper;
import fr.medes.android.maps.database.sqlite.PreCacheCursor;
import fr.medes.android.maps.overlay.BoundingBoxOverlay;

public class PreCacheMap extends MapActivity implements MapFragment.Callback {

	private static final int ZOOM_FOR_PRECACHE = 11;

	private String mTileSource = null;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public void onViewCreated(MapView view) {
		final Intent intent = getIntent();
		if (intent.getBooleanExtra(MapsConstants.EXTRA_SHOW_PRECACHE, false)) {
			showPreCache(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTileSource != null) {
			mMapFragment.getMapView().setTileSource(TileSourceFactory.getTileSource(mTileSource));
			mTileSource = null;
		}
	}

	private void showPreCache(final Intent intent) {
		MapsOpenHelper dbHelper = MapsOpenHelper.getInstance();
		PreCacheCursor c = new QueryBuilder(dbHelper, PreCache.Columns.TABLE_NAME)
				.setCursorFactory(new PreCacheCursor.Factory()).query();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			BoundingBoxOverlay bbo = new BoundingBoxOverlay(this);
			bbo.setBounds(c.getNorth(), c.getEast(), c.getSouth(), c.getWest());
			mMapFragment.getMapView().getOverlayManager().add(bbo);
		}
		c.close();

		mTileSource = intent.getStringExtra(MapsConstants.EXTRA_TILE_SOURCE);

		final double north = intent.getDoubleExtra(MapsConstants.EXTRA_LAT_NORTH, MapsConstants.DEFAULT_NORTH);
		final double east = intent.getDoubleExtra(MapsConstants.EXTRA_LON_EAST, MapsConstants.DEFAULT_EAST);
		final double south = intent.getDoubleExtra(MapsConstants.EXTRA_LAT_SOUTH, MapsConstants.DEFAULT_SOUTH);
		final double west = intent.getDoubleExtra(MapsConstants.EXTRA_LON_WEST, MapsConstants.DEFAULT_WEST);

		final GeoPoint center = new GeoPoint((north + south) / 2, (east + west) / 2);

		final MapView mapView = mMapFragment.getMapView();
		mapView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mapView.getController().setZoom(ZOOM_FOR_PRECACHE);
				mapView.getController().animateTo(center);
			}
		}, 500);
	}

}