package fr.medes.android.maps.app;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import fr.medes.android.app.MessageDialogFragment;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.database.PreCache;
import fr.medes.android.maps.database.sqlite.PreCacheCursor;

public class PreCacheListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private PreCacheAdapter mAdapter;

	private final View.OnClickListener mDeleteListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Bundle args = new Bundle();
			args.putString(MessageDialogFragment.ARG_TITLE, getString(android.R.string.dialog_alert_title));
			args.putString(MessageDialogFragment.ARG_MESSAGE, getString(R.string.maps__offline_delete));
			args.putBoolean(MessageDialogFragment.ARG_CANCEL, true);
			args.putParcelable(MessageDialogFragment.ARG_TAG, (Uri) v.getTag());

			MessageDialogFragment fragment = new MessageDialogFragment();
			fragment.setArguments(args);
			fragment.show(getFragmentManager(), "delete");
		}
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setHasOptionsMenu(true);

		setEmptyText(getString(R.string.maps__offline_noPreCacheHelpText));

		mAdapter = new PreCacheAdapter(getContext());
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.maps__precache_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.maps__offline_add) {
			startActivity(new Intent(getContext(), MapActivity.class));
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getContext(), PreCache.URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader loader, Cursor cursor) {
		mAdapter.changeCursor(new PreCacheCursor(cursor));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		PreCacheCursor c = (PreCacheCursor) l.getAdapter().getItem(position);

		Intent intent = new Intent(getContext(), PreCacheMap.class);
		intent.putExtra(MapsConstants.EXTRA_SHOW_PRECACHE, true);
		intent.putExtra(MapsConstants.EXTRA_LAT_NORTH, c.getNorth());
		intent.putExtra(MapsConstants.EXTRA_LON_EAST, c.getEast());
		intent.putExtra(MapsConstants.EXTRA_LAT_SOUTH, c.getSouth());
		intent.putExtra(MapsConstants.EXTRA_LON_WEST, c.getWest());
		intent.putExtra(MapsConstants.EXTRA_TILE_SOURCE, c.getProvider());

		startActivity(intent);
	}

	private class PreCacheAdapter extends CursorAdapter {

		public PreCacheAdapter(Context context) {
			super(context, null, true);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			PreCacheCursor c = (PreCacheCursor) cursor;

			TextView text1 = (TextView) view.findViewById(android.R.id.text1);
			text1.setText(getString(R.string.maps__offline_text1, c.getProvider(), c.getZoomMin(), c.getZoomMax()));

			TextView text2 = (TextView) view.findViewById(android.R.id.text2);
			text2.setText(getString(R.string.maps__offline_text2, c.getNorth(), c.getEast(), c.getSouth(), c.getWest()));

			View iconView = view.findViewById(android.R.id.icon);
			iconView.setTag(ContentUris.withAppendedId(PreCache.URI, c.getId()));
			iconView.setOnClickListener(mDeleteListener);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.maps__precache_item, parent, false);
		}
	}
}
