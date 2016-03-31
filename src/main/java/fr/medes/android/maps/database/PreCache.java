package fr.medes.android.maps.database;

import android.net.Uri;
import android.provider.BaseColumns;

import fr.medes.android.maps.content.MapsContentProvider;
import fr.medes.android.util.content.ContentUrisUtils;

public class PreCache {

	public static final String TABLE_NAME = "precache";
	public static final Uri URI = ContentUrisUtils.buildUriForFragment(MapsContentProvider.AUTHORITY, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String PROVIDER = "provider";
		public static final String NORTH = "north";
		public static final String EAST = "east";
		public static final String SOUTH = "south";
		public static final String WEST = "west";
		public static final String ZOOM_MIN = "zoomMin";
		public static final String ZOOM_MAX = "zoomMax";
	}

}
