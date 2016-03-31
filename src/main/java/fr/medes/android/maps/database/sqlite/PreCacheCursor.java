package fr.medes.android.maps.database.sqlite;

import android.database.Cursor;

import fr.medes.android.database.sqlite.BaseCursorWrapper;
import fr.medes.android.maps.database.PreCache;

public class PreCacheCursor extends BaseCursorWrapper {

	public PreCacheCursor(Cursor cursor) {
		super(cursor);
	}

	public long getId() {
		return getLong(PreCache.Columns._ID);
	}

	public String getProvider() {
		return getString(PreCache.Columns.PROVIDER);
	}

	public double getNorth() {
		return getDouble(PreCache.Columns.NORTH);
	}

	public double getEast() {
		return getDouble(PreCache.Columns.EAST);
	}

	public double getSouth() {
		return getDouble(PreCache.Columns.SOUTH);
	}

	public double getWest() {
		return getDouble(PreCache.Columns.WEST);
	}

	public int getZoomMin() {
		return getInt(PreCache.Columns.ZOOM_MIN);
	}

	public int getZoomMax() {
		return getInt(PreCache.Columns.ZOOM_MAX);
	}

}
