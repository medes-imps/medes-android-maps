package fr.medes.android.maps.database;

import android.net.Uri;
import android.provider.BaseColumns;

import fr.medes.android.maps.MapsConstants;
import fr.medes.android.util.content.ContentUrisUtils;

public class PreCache {

	public static class Columns implements BaseColumns {
		public static final String TABLE_NAME = "preCacheBounds";
		public static final Uri CONTENT_URI = ContentUrisUtils.buildUriForFragment(MapsConstants.AUTHORITY, TABLE_NAME);

		public static final String PROVIDER = "provider";
		public static final String NORTH = "north";
		public static final String EAST = "east";
		public static final String SOUTH = "south";
		public static final String WEST = "west";
		public static final String ZOOM_MIN = "zoomMin";
		public static final String ZOOM_MAX = "zoomMax";
	}

	public long _id = -1;
	public String provider;
	public double north;
	public double east;
	public double south;
	public double west;
	public int zoomMin;
	public int zoomMax;

}
