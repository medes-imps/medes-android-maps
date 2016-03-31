package fr.medes.android.maps.database.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fr.medes.android.database.sqlite.stmt.QueryBuilder;
import fr.medes.android.maps.database.PreCache;

public class MapsOpenHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "medes-maps.db";

	private static volatile MapsOpenHelper sInstance;

	public static MapsOpenHelper getInstance() {
		if (sInstance == null) {
			throw new IllegalArgumentException("MapsOpenHelper has not been instantiated");
		}
		return sInstance;
	}

	public static void init(Context context) {
		sInstance = new MapsOpenHelper(context);
	}

	private static final String DATABASE_CREATE_PRECACHEBOUNDS = "create table if not exists " + //
			PreCache.Columns.TABLE_NAME + " (" + //
			PreCache.Columns._ID + " integer primary key autoincrement, " + //
			PreCache.Columns.PROVIDER + " text, " + //
			PreCache.Columns.NORTH + " real, " + //
			PreCache.Columns.EAST + " real, " + //
			PreCache.Columns.SOUTH + " real, " + //
			PreCache.Columns.WEST + " real, " + //
			PreCache.Columns.ZOOM_MIN + " integer, " + //
			PreCache.Columns.ZOOM_MAX + " integer);";

	private MapsOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_PRECACHEBOUNDS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//TODO
	}

	public QueryBuilder createBuilder() {
		return new QueryBuilder(this, PreCache.Columns.TABLE_NAME);
	}

	public void upsert(PreCache cache) {
		ContentValues values = new ContentValues();
		values.put(PreCache.Columns.PROVIDER, cache.provider);
		values.put(PreCache.Columns.NORTH, cache.north);
		values.put(PreCache.Columns.EAST, cache.east);
		values.put(PreCache.Columns.SOUTH, cache.south);
		values.put(PreCache.Columns.WEST, cache.west);
		values.put(PreCache.Columns.ZOOM_MIN, cache.zoomMin);
		values.put(PreCache.Columns.ZOOM_MAX, cache.zoomMax);

		SQLiteDatabase db = getWritableDatabase();
		if (cache._id == -1) {
			cache._id = db.insert(PreCache.Columns.TABLE_NAME, null, values);
		} else {
			db.update(PreCache.Columns.TABLE_NAME, values, PreCache.Columns._ID + "=" + cache._id, null);
		}
	}

	public void delete(long id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(PreCache.Columns.TABLE_NAME, PreCache.Columns._ID + "=" + id, null);
	}

}
