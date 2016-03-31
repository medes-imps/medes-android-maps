package fr.medes.android.maps.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

public class TileLoaderDialogFragment extends DialogFragment implements TileLoaderDialog.OnSettingsSetListener {

	private static final String ARG_TILE_SOURCE_NAME = "tileSourceName";
	private static final String ARG_LATITUTE = "latitude";
	private static final String ARG_LONGITUDE = "longitude";

	public static TileLoaderDialogFragment newInstance(String tileSourceName, double latitude, double longitude) {
		Bundle bundle = new Bundle();
		bundle.putString(ARG_TILE_SOURCE_NAME, tileSourceName);
		bundle.putDouble(ARG_LATITUTE, latitude);
		bundle.putDouble(ARG_LONGITUDE, longitude);
		TileLoaderDialogFragment fragment = new TileLoaderDialogFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	private Callback mCallback;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String name = getArguments().getString(ARG_TILE_SOURCE_NAME);
		ITileSource source = TileSourceFactory.getTileSource(name);
		return new TileLoaderDialog(getContext(), this, source.getMinimumZoomLevel(), source.getMaximumZoomLevel());
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		// Activities containing this fragment must implement its callbacks.
		if (!(context instanceof Callback)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallback = (Callback) context;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		mCallback = null;
	}

	@Override
	public void onSettingsSet(double distance, int zoomMin, int zoomMax) {
		if (mCallback == null) {
			return;
		}
		String tileSourceName = getArguments().getString(ARG_TILE_SOURCE_NAME);
		double latitude = getArguments().getDouble(ARG_LATITUTE);
		double longitude = getArguments().getDouble(ARG_LONGITUDE);
		mCallback.onSettingsSet(tileSourceName, latitude, longitude, distance, zoomMin, zoomMax);
	}

	public interface Callback {
		void onSettingsSet(String tileSourceName, double latitude, double longitude, double distance, int zoomMin, int zoomMax);
	}
}
