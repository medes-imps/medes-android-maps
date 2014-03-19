package fr.medes.android.maps.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import fr.medes.android.app.GeoPickerDialog;
import fr.medes.android.app.GeoPickerDialog.OnGeoSetListener;
import fr.medes.android.maps.MapsConstants;
import fr.medes.android.widget.GeoPicker;

public class EnterManualLocation extends Activity implements OnGeoSetListener, OnCancelListener {

	private static final String EXTRA_DIALOG_SHOWING = "EnterManualLocation_dialogShowing";

	private static final int DIALOG_PICKER_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			showDialog(DIALOG_PICKER_ID);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_DIALOG_SHOWING, true);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PICKER_ID:
			Dialog dialog = new GeoPickerDialog(this, this, 0, 0);
			dialog.setOnCancelListener(this);
			return dialog;
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	public void onGeoSet(GeoPicker view, Double latitude, Double longitude) {
		Location location = new Location("manually");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		setResult(RESULT_OK, new Intent().putExtra(MapsConstants.EXTRA_LOCATION, location));
		finish();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		setResult(RESULT_CANCELED);
		finish();
	}

}
