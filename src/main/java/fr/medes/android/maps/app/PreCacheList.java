package fr.medes.android.maps.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import fr.medes.android.app.AmlApplication;
import fr.medes.android.database.sqlite.AmlOpenHelper;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.database.PreCache;
import fr.medes.android.maps.database.sqlite.PreCacheCursor;

public class PreCacheList extends ListActivity {

	private static final int DIALOG_DELETE_ID = 1;

	private long mPreCache = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.maps__list_content);

		Cursor c = getOpenHelper().query(PreCache.Columns.CONTENT_URI);
		startManagingCursor(c);

		PreCacheAdapter adapter = new PreCacheAdapter(this, c);
		setListAdapter(adapter);

		TextView empty = (TextView) findViewById(R.id.maps__emptyText);
		empty.setText(R.string.maps__offline_noPreCacheHelpText);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_DELETE_ID:
				return new AlertDialog.Builder(this).setTitle(android.R.string.dialog_alert_title)
						.setMessage(R.string.maps__offline_delete).setPositiveButton(android.R.string.ok, mDialogListener)
						.setNegativeButton(android.R.string.cancel, null).create();
			default:
				return super.onCreateDialog(id);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		PreCacheCursor c = (PreCacheCursor) l.getAdapter().getItem(position);

		Intent intent = new Intent(this, PreCacheMap.class);
		intent.putExtra(MapsConstants.EXTRA_SHOW_PRECACHE, true);
		intent.putExtra(MapsConstants.EXTRA_LAT_NORTH, c.getNorth());
		intent.putExtra(MapsConstants.EXTRA_LON_EAST, c.getEast());
		intent.putExtra(MapsConstants.EXTRA_LAT_SOUTH, c.getSouth());
		intent.putExtra(MapsConstants.EXTRA_LON_WEST, c.getWest());
		intent.putExtra(MapsConstants.EXTRA_TILE_SOURCE, c.getProvider());

		startActivity(intent);
	}

	private AmlOpenHelper getOpenHelper() {
		return ((AmlApplication) getApplication()).getOpenHelper();
	}

	private class PreCacheAdapter extends CursorAdapter {

		public PreCacheAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			PreCacheCursor c = (PreCacheCursor) cursor;

			TextView text1 = (TextView) view.findViewById(android.R.id.text1);
			text1.setText(getString(R.string.maps__offline_text1, c.getProvider(), c.getZoomMin(), c.getZoomMax()));

			TextView text2 = (TextView) view.findViewById(android.R.id.text2);
			text2.setText(getString(R.string.maps__offline_text2, c.getNorth(), c.getEast(), c.getSouth(), c.getWest()));

			View iconView = view.findViewById(android.R.id.icon);
			iconView.setTag(c.getId());
			iconView.setOnClickListener(mDeleteListener);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.maps__precache_item, parent, false);
		}
	}

	private final DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (mPreCache < 0) {
				return;
			}
			getContentResolver().delete(PreCache.Columns.CONTENT_URI, PreCache.Columns._ID + "=" + mPreCache, null);
		}
	};

	private final View.OnClickListener mDeleteListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mPreCache = (Long) v.getTag();
			showDialog(DIALOG_DELETE_ID);
		}
	};

}
