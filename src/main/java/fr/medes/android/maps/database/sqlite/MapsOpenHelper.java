package fr.medes.android.maps.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fr.medes.android.maps.database.PreCache;

public class MapsOpenHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "medes-maps.db";

	private static final String DATABASE_CREATE_PRECACHE = "create table if not exists " + //
			PreCache.TABLE_NAME + " (" + //
			PreCache.Columns._ID + " integer primary key autoincrement, " + //
			PreCache.Columns.PROVIDER + " text, " + //
			PreCache.Columns.NORTH + " real, " + //
			PreCache.Columns.EAST + " real, " + //
			PreCache.Columns.SOUTH + " real, " + //
			PreCache.Columns.WEST + " real, " + //
			PreCache.Columns.ZOOM_MIN + " integer, " + //
			PreCache.Columns.ZOOM_MAX + " integer);";

	public MapsOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_PRECACHE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DATABASE_CREATE_PRECACHE);
	}

}
