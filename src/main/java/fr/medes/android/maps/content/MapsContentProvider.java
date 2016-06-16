package fr.medes.android.maps.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import fr.medes.android.BuildConfigHelper;
import fr.medes.android.maps.database.PreCache;
import fr.medes.android.maps.database.sqlite.MapsOpenHelper;

public class MapsContentProvider extends ContentProvider {

	public static final String AUTHORITY = BuildConfigHelper.APPLICATION_ID + ".mapsprovider";

	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private static final int PRECACHE = 1;
	private static final int PRECACHE_ID = 2;

	static {
		sUriMatcher.addURI(AUTHORITY, PreCache.TABLE_NAME, PRECACHE);
		sUriMatcher.addURI(AUTHORITY, PreCache.TABLE_NAME + "/#", PRECACHE_ID);
	}

	private static final String VND_ITEM = "vnd.medes.maps.item/";
	private static final String VND_DIR = "vnd.medes.maps.dir/";

	private MapsOpenHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new MapsOpenHelper(getContext());
		return true;
	}

	@Nullable
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case PRECACHE:
				return VND_DIR + PreCache.TABLE_NAME;
			case PRECACHE_ID:
				return VND_ITEM + PreCache.TABLE_NAME;
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
			case PRECACHE:
				qb.setTables(PreCache.TABLE_NAME);
				break;
			case PRECACHE_ID:
				qb.setTables(PreCache.TABLE_NAME);
				qb.appendWhere(PreCache.Columns._ID + "=" + uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int result = -1;
		switch (sUriMatcher.match(uri)) {
			case PRECACHE:
				result = db.update(PreCache.TABLE_NAME, values, selection, selectionArgs);
				break;
			case PRECACHE_ID:
				result = db.update(PreCache.TABLE_NAME, values, PreCache.Columns._ID + "=" + uri.getLastPathSegment(), null);
				break;
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
			case PRECACHE:
				long rowId = db.insert(PreCache.TABLE_NAME, null, values);
				if (rowId != -1) {
					Uri rowUri = ContentUris.withAppendedId(PreCache.URI, rowId);
					getContext().getContentResolver().notifyChange(rowUri, null);
					return rowUri;
				}
				throw new SQLException("Failed to insert row into " + PreCache.TABLE_NAME);
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = -1;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
			case PRECACHE:
				result = db.delete(PreCache.TABLE_NAME, selection, selectionArgs);
				break;
			case PRECACHE_ID:
				result = db.delete(PreCache.TABLE_NAME, PreCache.Columns._ID + "=" + uri.getLastPathSegment(), null);
				break;
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}
}
