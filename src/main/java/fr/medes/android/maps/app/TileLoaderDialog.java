package fr.medes.android.maps.app;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import fr.medes.android.maps.R;

public class TileLoaderDialog extends AlertDialog implements DialogInterface.OnClickListener {

	public interface OnSettingsSetListener {
		void onSettingsSet(double distance, int zoomMin, int zoomMax);
	}

	private final Spinner mZoomMinSpinner;
	private final Spinner mZoomMaxSpinner;
	private final Spinner mAreaSpinner;

	private final OnSettingsSetListener mOnSettingsSetListener;

	public TileLoaderDialog(Context context, OnSettingsSetListener listener, int zoomMin, int zoomMax) {
		this(context, listener, 0, zoomMin, zoomMax);
	}

	public TileLoaderDialog(Context context, OnSettingsSetListener listener, int theme, int zoomMin, int zoomMax) {
		super(context, theme);

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.maps__tile_loader_dialog, null);
		setView(view);

		mOnSettingsSetListener = listener;

		ArrayAdapter<Integer> zoomAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item);
		zoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (int i = zoomMin; i <= zoomMax; i++) {
			zoomAdapter.add(i);
		}

		mZoomMinSpinner = (Spinner) view.findViewById(R.id.maps__offline_zoommin);
		mZoomMaxSpinner = (Spinner) view.findViewById(R.id.maps__offline_zoommax);
		mAreaSpinner = (Spinner) view.findViewById(R.id.maps__offline_area);

		mZoomMinSpinner.setAdapter(zoomAdapter);
		mZoomMaxSpinner.setAdapter(zoomAdapter);
		mZoomMaxSpinner.setSelection(zoomAdapter.getCount() - 1);

		setButton(DialogInterface.BUTTON_POSITIVE, context.getString(android.R.string.ok), this);
		setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			int zoomMin = (Integer) mZoomMinSpinner.getSelectedItem();
			int zoomMax = (Integer) mZoomMaxSpinner.getSelectedItem();
			if (zoomMin < zoomMax) {
				final double distance = (1 << mAreaSpinner.getSelectedItemPosition() + 1) * 1000;
				mOnSettingsSetListener.onSettingsSet(distance, zoomMin, zoomMax);
			}
		}
	}
}
