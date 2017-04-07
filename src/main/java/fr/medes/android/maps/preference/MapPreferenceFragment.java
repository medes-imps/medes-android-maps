package fr.medes.android.maps.preference;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import org.osmdroid.config.Configuration;

import java.text.MessageFormat;

import fr.medes.android.app.WakefulIntentService;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.database.PreCache;
import fr.medes.android.maps.database.sqlite.PreCacheCursor;
import fr.medes.android.maps.offline.TileLoaderService;
import fr.medes.android.os.BaseAsyncTask;
import fr.medes.android.preference.MyPreferenceFragmentCompat;
import fr.medes.android.util.file.FileUtils;

public class MapPreferenceFragment extends MyPreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

	private static final int REQUEST_PERMISSION_UPDATE_CACHE_SIZE = 1;
	private static final int REQUEST_PERMISSION_CLEAR_CACHE = 2;

	private Preference mMapAutomaticCache;
	private Preference mMapClearCache;
	private Preference mMapPrecacheArea;

	private UpdatePrecacheAreaTask mUpdatePrecacheAreaTask;
	private ClearCacheTask mClearCacheTask;
	private AutomaticCacheTask mAutomaticCacheTask;
	private UpdateCacheSizeTask mUpdateCacheSizeTask;

	private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			executeUpdatePrecacheArea();
		}
	};

	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		Bundle args = getArguments();

		if (args != null && args.getBoolean(ARG_SHOULD_INFLATE, true)) {
			addPreferencesFromResource(R.xml.maps__preferences);

			mMapAutomaticCache = findPreference(R.string.maps__pref_automatic_cache);
			mMapClearCache = findPreference(R.string.maps__pref_clear_cache);
			mMapPrecacheArea = findPreference(R.string.maps__pref_precache_area);
		}

		refreshPreferenceState();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mUpdatePrecacheAreaTask != null) {
			mUpdatePrecacheAreaTask.setCallback(mUpdatePrecacheAreaCallback);
		}
		if (mClearCacheTask != null) {
			mClearCacheTask.setCallback(mClearCacheCallback);
		}
		if (mUpdateCacheSizeTask != null) {
			mUpdateCacheSizeTask.setCallback(mUpdateCacheSizeCallback);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mUpdatePrecacheAreaTask != null) {
			mUpdatePrecacheAreaTask.setCallback(null);
		}
		if (mClearCacheTask != null) {
			mClearCacheTask.setCallback(null);
		}
		if (mUpdateCacheSizeTask != null) {
			mUpdateCacheSizeTask.setCallback(null);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		getContext().getContentResolver().unregisterContentObserver(mContentObserver);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == mMapAutomaticCache) {
			executeAutomaticCache();
			return true;
		} else if (preference == mMapClearCache) {
			executeClearCache();
			return true;
		}
		return false;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_PERMISSION_UPDATE_CACHE_SIZE:
				if (checkPermissionsGranted(grantResults)) {
					executeUpdateCacheSize();
				}
				break;
			case REQUEST_PERMISSION_CLEAR_CACHE:
				if (checkPermissionsGranted(grantResults)) {
					executeClearCache();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	public void setPreferences(Preference automaticCache, Preference clearCache, Preference preacacheArea) {
		mMapAutomaticCache = automaticCache;
		mMapClearCache = clearCache;
		mMapPrecacheArea = preacacheArea;

		refreshPreferenceState();
	}

	private void refreshPreferenceState() {
		if (mMapAutomaticCache == null || mMapClearCache == null || mMapPrecacheArea == null) {
			return;
		}
		mMapAutomaticCache.setOnPreferenceClickListener(this);
		mMapClearCache.setOnPreferenceClickListener(this);

		getContext().getContentResolver().registerContentObserver(PreCache.URI, true, mContentObserver);

		executeUpdateCacheSize();
		executeUpdatePrecacheArea();

		setRetainInstance(true);
	}

	private void executeAutomaticCache() {
		mAutomaticCacheTask = new AutomaticCacheTask(getContext());
		mAutomaticCacheTask.execute();
	}

	private void executeUpdateCacheSize() {
		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_UPDATE_CACHE_SIZE);
			return;
		}
		mUpdateCacheSizeTask = new UpdateCacheSizeTask();
		mUpdateCacheSizeTask.setCallback(mUpdateCacheSizeCallback);
		mUpdateCacheSizeTask.execute();
	}

	private void executeClearCache() {
		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CLEAR_CACHE);
			return;
		}
		mClearCacheTask = new ClearCacheTask();
		mClearCacheTask.setCallback(mClearCacheCallback);
		mClearCacheTask.execute();
	}

	private void executeUpdatePrecacheArea() {
		mUpdatePrecacheAreaTask = new UpdatePrecacheAreaTask(getContext());
		mUpdatePrecacheAreaTask.setCallback(mUpdatePrecacheAreaCallback);
		mUpdatePrecacheAreaTask.execute();
	}

	private void setCacheSizeSummary(long size) {
		String readableSize = FileUtils.readableFileSize(size);
		String summary = getString(R.string.maps__preferences_clear_summary, readableSize);
		mMapClearCache.setSummary(summary);
	}

	private void setCacheSizeEnabled(boolean enabled) {
		mMapClearCache.setEnabled(enabled);
	}

	private void setPrecacheAreaSummary(int count) {
		String fmt = getResources().getString(R.string.maps__preferences_area_summary);
		mMapPrecacheArea.setSummary(MessageFormat.format(fmt, count));
	}

	private boolean checkPermissionsGranted(int[] grantResults) {
		for (int grant : grantResults) {
			if (grant != PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(getContext(), R.string.aml__permission_not_granted, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Callbacks
	////////////////////////////////////////////////////////////////////////////////////////////////


	private final BaseAsyncTask.Callback<Void, Void, Long> mUpdateCacheSizeCallback = new BaseAsyncTask.BaseCallback<Void, Void, Long>() {

		@Override
		public void onAttachedToTask(AsyncTask.Status status, Long result) {
			if (status == AsyncTask.Status.FINISHED) {
				setCacheSizeSummary(result);
			}
		}

		@Override
		public void onPostExecute(Long result) {
			setCacheSizeSummary(result);
		}

	};

	private final BaseAsyncTask.Callback<Void, Void, Long> mClearCacheCallback = new BaseAsyncTask.BaseCallback<Void, Void, Long>() {

		@Override
		public void onAttachedToTask(AsyncTask.Status status, Long result) {
			if (status == AsyncTask.Status.FINISHED) {
				onPostExecute(result);
			} else if (status == AsyncTask.Status.RUNNING) {
				setCacheSizeEnabled(false);
			}
		}

		@Override
		public void onPostExecute(Long result) {
			setCacheSizeEnabled(true);
			setCacheSizeSummary(result);
		}

		@Override
		public void onPreExecute() {
			setCacheSizeEnabled(false);
		}

	};

	private final BaseAsyncTask.Callback<Void, Void, Integer> mUpdatePrecacheAreaCallback = new BaseAsyncTask.BaseCallback<Void, Void, Integer>() {

		@Override
		public void onAttachedToTask(AsyncTask.Status status, Integer result) {
			if (status == AsyncTask.Status.FINISHED) {
				setPrecacheAreaSummary(result);
			}
		}

		@Override
		public void onPostExecute(Integer result) {
			setPrecacheAreaSummary(result);
		}
	};

	private static long blockingCacheSize() {
		return FileUtils.getDirectorySize(Configuration.getInstance().getOsmdroidTileCache());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Inner private classes
	////////////////////////////////////////////////////////////////////////////////////////////////


	private class AutomaticCacheTask extends BaseAsyncTask<Void, Void, Void> {

		private final Context context;

		private AutomaticCacheTask(Context context) {
			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Cursor innerCursor = context.getContentResolver().query(PreCache.URI, null, null, null, null);
			PreCacheCursor c = new PreCacheCursor(innerCursor);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				Intent intent = new Intent(context, TileLoaderService.class);
				intent.putExtra(MapsConstants.EXTRA_REFRESH_PRECACHE, true);
				intent.putExtra(MapsConstants.EXTRA_TILE_SOURCE, c.getProvider());
				intent.putExtra(MapsConstants.EXTRA_LAT_NORTH, c.getNorth());
				intent.putExtra(MapsConstants.EXTRA_LAT_SOUTH, c.getSouth());
				intent.putExtra(MapsConstants.EXTRA_LON_EAST, c.getEast());
				intent.putExtra(MapsConstants.EXTRA_LON_WEST, c.getWest());
				intent.putExtra(MapsConstants.EXTRA_ZOOM_MIN, c.getZoomMin());
				intent.putExtra(MapsConstants.EXTRA_ZOOM_MAX, c.getZoomMax());

				WakefulIntentService.sendWakefulWork(context, intent);
			}
			c.close();
			return null;
		}

	}


	private static class UpdateCacheSizeTask extends BaseAsyncTask<Void, Void, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			return blockingCacheSize();
		}

	}


	private static class ClearCacheTask extends BaseAsyncTask<Void, Void, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			FileUtils.deleteDirectory(Configuration.getInstance().getOsmdroidTileCache());
			return blockingCacheSize();
		}

	}


	private class UpdatePrecacheAreaTask extends BaseAsyncTask<Void, Void, Integer> {

		private final Context context;

		private UpdatePrecacheAreaTask(Context context) {
			this.context = context;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int result = 0;
			Cursor c = context.getContentResolver().query(PreCache.URI, null, null, null, null);
			if (c != null) {
				result = c.getCount();
				c.close();
			}
			return result;
		}

	}

}
