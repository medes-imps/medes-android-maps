package fr.medes.android.maps.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import fr.medes.android.app.SingleChoiceDialogFragment;
import fr.medes.android.maps.R;
import fr.medes.android.maps.overlay.LongClickableOverlay;
import fr.medes.android.maps.util.TileSourceUtil;
import fr.medes.android.maps.widget.BalloonView;

public class MapFragment extends Fragment implements MapListener,
		LongClickableOverlay.OnLongClickListener, View.OnClickListener {

	private static final String EXTRA_SHOW_LOCATION = "showLocation";

	private static final String PREFERENCES_NAME = "name";
	private static final String PREFERENCE_ZOOM_LEVEL = "zoomLevel";
	private static final String PREFERENCE_SCROLL_X = "scrollX";
	private static final String PREFERENCE_SCROLL_Y = "scrollY";
	private static final String PREFERENCE_TILE_SOURCE = "tileSource";

	private ResourceProxy mResourceProxy;


	private MyLocationNewOverlay mLocationOverlay;
	private LongClickableOverlay mLongClickableOverlay;

	private BalloonView mBalloonView;

	private SharedPreferences mPrefs;

	private boolean mPrecachingEnabled = true;

	private Callback mCallback;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// only do static initialisation if needed
		if (CloudmadeUtil.getCloudmadeKey().length() == 0) {
			CloudmadeUtil.retrieveCloudmadeKey(getContext().getApplicationContext());
		}

		mPrefs = getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		mResourceProxy = new ResourceProxyImpl(getContext());

		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MapView view = new MapView(getContext(), 256, mResourceProxy);
		view.setBuiltInZoomControls(true);
		view.setMultiTouchControls(true);

		view.getController().setZoom(mPrefs.getInt(PREFERENCE_ZOOM_LEVEL, 1));
		view.scrollTo(mPrefs.getInt(PREFERENCE_SCROLL_X, 0), mPrefs.getInt(PREFERENCE_SCROLL_Y, 0));

		mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), view, mResourceProxy);
		view.getOverlayManager().add(mLocationOverlay);

		mLongClickableOverlay = new LongClickableOverlay(getContext(), this);
		if (isPrecachingEnabled()) {
			view.getOverlayManager().add(mLongClickableOverlay);
		}

		view.setMapListener(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (mCallback != null) {
			mCallback.onViewCreated((MapView) view);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.maps__map, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.maps__menu_place) {
			changeMyLocationState();
			return true;
		} else if (item.getItemId() == R.id.maps__menu_mapmode) {
			SingleChoiceDialogFragment fragment = SingleChoiceDialogFragment.newInstance(
					getString(R.string.maps__menu_mapmode),
					TileSourceUtil.readableTileSources(mResourceProxy), -1, null);
			fragment.show(getFragmentManager(), "mapMode");
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_SHOW_LOCATION, mLocationOverlay.isMyLocationEnabled());

		mLocationOverlay.disableMyLocation();
	}

	@Override
	public void onPause() {
		MapView view = getMapView();
		final SharedPreferences.Editor edit = mPrefs.edit();
		edit.putString(PREFERENCE_TILE_SOURCE, view.getTileProvider().getTileSource().name());
		edit.putInt(PREFERENCE_SCROLL_X, view.getScrollX());
		edit.putInt(PREFERENCE_SCROLL_Y, view.getScrollY());
		edit.putInt(PREFERENCE_ZOOM_LEVEL, view.getZoomLevel());
		edit.apply();

		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		final String tileSourceName = mPrefs.getString(PREFERENCE_TILE_SOURCE, TileSourceFactory.DEFAULT_TILE_SOURCE.name());
		try {
			final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
			getMapView().setTileSource(tileSource);
		} catch (final IllegalArgumentException ignore) {
		}
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getBoolean(EXTRA_SHOW_LOCATION)) {
			mLocationOverlay.enableMyLocation();
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof Callback) {
			mCallback = (Callback) context;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		mCallback = null;
	}

	public MapView getMapView() {
		return (MapView) getView();
	}

	public void changeMyLocationState() {
		if (mLocationOverlay.isMyLocationEnabled()) {
			mLocationOverlay.disableMyLocation();
			Toast.makeText(getContext(), R.string.maps__set_mode_hide_me, Toast.LENGTH_LONG).show();
		} else {
			mLocationOverlay.enableMyLocation();
			Toast.makeText(getContext(), R.string.maps__set_mode_show_me, Toast.LENGTH_LONG).show();
		}
	}

	public void changeConnectionState() {
		MapView view = getMapView();
		final boolean useDataConnection = !view.useDataConnection();
		view.setUseDataConnection(useDataConnection);
		final int id = useDataConnection ? R.string.maps__set_mode_online : R.string.maps__set_mode_offline;
		Toast.makeText(getContext(), id, Toast.LENGTH_LONG).show();
	}

	public void setPrecachingEnabled(boolean enabled) {
		if (mPrecachingEnabled == enabled) {
			return;
		}
		mPrecachingEnabled = enabled;
		if (enabled) {
			getMapView().getOverlayManager().add(mLocationOverlay);
		} else {
			getMapView().getOverlayManager().remove(mLongClickableOverlay);
		}
	}

	public boolean isPrecachingEnabled() {
		return mPrecachingEnabled;
	}

	public void autozoom(IGeoPoint point) {
		getMapView().getController().animateTo(point);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Long click overlay listener implementation
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onLongClick(MapView mapView, IGeoPoint point) {
		boolean isRecycled = true;
		if (mBalloonView == null) {
			mBalloonView = new BalloonView(getContext());
			mBalloonView.setData(getString(R.string.maps__offline_title), getString(R.string.maps__offline_description));
			mBalloonView.setOnClickListener(this);
			isRecycled = false;
		}

		MapView.LayoutParams params = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, point, MapView.LayoutParams.BOTTOM_CENTER, 0, 0);

		mBalloonView.setTag(point);
		mBalloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			mBalloonView.setLayoutParams(params);
		} else {
			getMapView().addView(mBalloonView, params);
		}

		autozoom(point);
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Map listener implementation
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onZoom(ZoomEvent event) {
		int count = getMapView().getChildCount();
		for (int i = 0; i < count; i++) {
			getMapView().getChildAt(i).requestLayout();
		}
		return false;
	}

	@Override
	public boolean onScroll(ScrollEvent event) {
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// View click listener implementation
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onClick(View v) {
		if (v == mBalloonView) {
			final IGeoPoint point = (IGeoPoint) v.getTag();
			String tileSourceName = getMapView().getTileProvider().getTileSource().name();
			TileLoaderDialogFragment fragment = TileLoaderDialogFragment.newInstance(tileSourceName,
					point.getLatitude(), point.getLongitude());
			fragment.show(getFragmentManager(), "tileSettings");
			mBalloonView.setVisibility(View.GONE);
		}
	}

	public interface Callback {
		void onViewCreated(MapView view);
	}

}
