package fr.medes.android.maps.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.os.CancellationSignal;
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
import fr.medes.android.content.SimpleCursorLoader;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.maps.R;
import fr.medes.android.maps.database.sqlite.MapsOpenHelper;
import fr.medes.android.maps.database.sqlite.PreCacheCursor;

public class PreCacheListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<PreCacheCursor> {

	private PreCacheAdapter mAdapter;

	private final View.OnClickListener mDeleteListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Bundle args = new Bundle();
			args.putString(MessageDialogFragment.ARG_TITLE, getString(android.R.string.dialog_alert_title));
			args.putString(MessageDialogFragment.ARG_MESSAGE, getString(R.string.maps__offline_delete));
			args.putBoolean(MessageDialogFragment.ARG_CANCEL, true);
			args.putLong(MessageDialogFragment.ARG_TAG, (Long) v.getTag());

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
	public Loader<PreCacheCursor> onCreateLoader(int id, Bundle args) {
		return new PreCacheCursorLoader(getContext());
	}

	@Override
	public void onLoadFinished(Loader loader, PreCacheCursor cursor) {
		mAdapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<PreCacheCursor> loader) {
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

	public static class PreCacheCursorLoader extends SimpleCursorLoader<PreCacheCursor> {

		public PreCacheCursorLoader(Context context) {
			super(context);
		}

		@Override
		protected PreCacheCursor getCursor(Context context, CancellationSignal cancellationSignal) {
			PreCacheCursor cursor = MapsOpenHelper.getInstance().createBuilder()
					.setCursorFactory(new PreCacheCursor.Factory())
					.query();
			return cursor;
		}
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
			iconView.setTag(c.getId());
			iconView.setOnClickListener(mDeleteListener);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.maps__precache_item, parent, false);
		}
	}
}
