package fr.medes.android.maps.offline;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.ResourceProxyImpl;

import fr.medes.android.app.WakefulIntentService;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.app.PreCacheMap;
import fr.medes.android.maps.database.PreCache;
import fr.medes.android.maps.offline.TileLoaderManager.OnTileLoadedListener;

public class TileLoaderService extends WakefulIntentService implements OnTileLoadedListener {

	private static final String ACTION_CANCEL = "fr.medes.maps.TileLoaderService_cancel";

	public interface TileLoaderServiceListener {

		void updateLoadedTilesCount(int loaded);

		void finished();
	}

	private static final int NOTIFICATION_DOWNLOAD_ID = 654875;

	private final TileLoaderBinder mBinder = new TileLoaderBinder();

	private long lastTime = -1L;

	private NotificationManager mManager;
	private NotificationCompat.Builder mBuilder;

	private TileLoaderManager mLoaderManager;
	private int mTilesToDownload;

	private TileLoaderServiceListener mListener;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			stop();
		}
	};

	public TileLoaderService() {
		super(TileLoaderService.class.getName());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_CANCEL));

		final boolean refresh = intent.getBooleanExtra(MapsConstants.EXTRA_REFRESH_PRECACHE, false);
		final double north = intent.getDoubleExtra(MapsConstants.EXTRA_LAT_NORTH, MapsConstants.DEFAULT_NORTH);
		final double south = intent.getDoubleExtra(MapsConstants.EXTRA_LAT_SOUTH, MapsConstants.DEFAULT_SOUTH);
		final double east = intent.getDoubleExtra(MapsConstants.EXTRA_LON_EAST, MapsConstants.DEFAULT_EAST);
		final double west = intent.getDoubleExtra(MapsConstants.EXTRA_LON_WEST, MapsConstants.DEFAULT_WEST);
		final int zoomMin = intent.getIntExtra(MapsConstants.EXTRA_ZOOM_MIN, 0);
		final int zoomMax = intent.getIntExtra(MapsConstants.EXTRA_ZOOM_MAX, 18);

		OnlineTileSourceBase source = TileSourceFactory.MAPNIK;
		if (intent.hasExtra(MapsConstants.EXTRA_TILE_SOURCE)) {
			final String name = intent.getStringExtra(MapsConstants.EXTRA_TILE_SOURCE);
			ITileSource s = TileSourceFactory.getTileSource(name);
			if (s instanceof OnlineTileSourceBase) {
				source = (OnlineTileSourceBase) s;
			}
		}

		mLoaderManager = new TileLoaderManager(source, east, north, south, west, zoomMin, zoomMax);
		mTilesToDownload = mLoaderManager.getTilesToDownloadCount();

		showDownloadNotification(source.localizedName(new ResourceProxyImpl(this)));
		publishProgress(0);

		mLoaderManager.setOnTileLoadedListener(this);
		mLoaderManager.requestTiles();

		if (mListener != null) {
			mListener.finished();
		}
		unregisterReceiver(mBroadcastReceiver);

		if (refresh || mLoaderManager.hasBeenStopped()) {
			mManager.cancel(NOTIFICATION_DOWNLOAD_ID);
			return;
		}

		ContentValues values = new ContentValues();
		values.put(PreCache.Columns.PROVIDER, source.name());
		values.put(PreCache.Columns.NORTH, north);
		values.put(PreCache.Columns.EAST, east);
		values.put(PreCache.Columns.SOUTH, south);
		values.put(PreCache.Columns.WEST, west);
		values.put(PreCache.Columns.ZOOM_MIN, zoomMin);
		values.put(PreCache.Columns.ZOOM_MAX, zoomMax);

		getContentResolver().insert(PreCache.URI, values);

		Intent clickIntent = new Intent(this, PreCacheMap.class);
		clickIntent.putExtra(MapsConstants.EXTRA_SHOW_PRECACHE, true);
		clickIntent.putExtra(MapsConstants.EXTRA_LAT_NORTH, north);
		clickIntent.putExtra(MapsConstants.EXTRA_LON_EAST, east);
		clickIntent.putExtra(MapsConstants.EXTRA_LAT_SOUTH, south);
		clickIntent.putExtra(MapsConstants.EXTRA_LON_WEST, west);
		clickIntent.putExtra(MapsConstants.EXTRA_TILE_SOURCE, source.name());

		String title = getString(R.string.maps__offline_text1, source.name(), zoomMin, zoomMax);
		String text = getString(R.string.maps__offline_text2, north, east, south, west);
		showDownloadFinishedNotification(title, text, clickIntent);
	}

	public void stop() {
		if (mLoaderManager != null) {
			mLoaderManager.stop();
		}
	}

	public int getTilesToDownloadCount() {
		if (mLoaderManager != null) {
			return mLoaderManager.getTilesToDownloadCount();
		}
		return 100;
	}

	public int getTilesDownloadedCount() {
		if (mLoaderManager != null) {
			return mLoaderManager.getTilesDownloadedCount();
		}
		return 0;
	}

	public void setTileLoaderServiceListener(TileLoaderServiceListener listener) {
		mListener = listener;
	}

	@Override
	public void onTileLoaded(MapTile tile) {
		long time = System.currentTimeMillis();
		if (time - lastTime > 1000) {
			if (mListener != null) {
				mListener.updateLoadedTilesCount(mLoaderManager.getTilesDownloadedCount());
			}
			publishProgress(mLoaderManager.getTilesDownloadedCount());
			lastTime = time;
		}
	}

	private void publishProgress(int progress) {
		if (mBuilder == null) {
			return;
		}
		mBuilder.setContentText(getString(R.string.maps__offline_size, progress, mTilesToDownload))
				.setContentInfo(getString(R.string.maps__offline_percent, progress * 100 / mTilesToDownload))
				.setProgress(mTilesToDownload, progress, false);
		mManager.notify(NOTIFICATION_DOWNLOAD_ID, mBuilder.build());
	}

	private void showDownloadNotification(String label) {
		Intent resultIntent = new Intent(this, TileLoaderActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(TileLoaderActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		Intent cancelintent = new Intent(ACTION_CANCEL);
		PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, 0, cancelintent, 0);

		mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle(label)
				.setContentText(getString(R.string.maps__offline_title))
				.setContentInfo(null)
				.setProgress(mTilesToDownload, 0, false)
				.setSmallIcon(R.drawable.maps__map)
				.setContentIntent(resultPendingIntent)
				.addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(android.R.string.cancel), cancelPendingIntent)
				.setOngoing(true);

		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mManager.notify(NOTIFICATION_DOWNLOAD_ID, mBuilder.build());
	}

	private void showDownloadFinishedNotification(String title, String text, Intent clickIntent) {
		if (mBuilder == null) {
			return;
		}
		mBuilder.setOngoing(false)
				.setAutoCancel(true)
				.setProgress(0, 0, false)
				.setContentTitle(title)
				.setContentText(text)
				.setContentInfo(null)
				.setContentIntent(PendingIntent.getActivity(this, 0, clickIntent, 0))
				.mActions.clear();
		mManager.notify(NOTIFICATION_DOWNLOAD_ID, mBuilder.build());
	}

	public class TileLoaderBinder extends Binder {

		public TileLoaderService getService() {
			return TileLoaderService.this;
		}

	}

}
