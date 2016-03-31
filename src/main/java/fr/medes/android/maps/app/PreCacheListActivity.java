package fr.medes.android.maps.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import fr.medes.android.app.MessageDialogFragment;
import fr.medes.android.maps.database.sqlite.MapsOpenHelper;

public class PreCacheListActivity extends AppCompatActivity implements MessageDialogFragment.Callback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PreCacheListFragment())
				.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClickDialog(int which, Object tag) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			MapsOpenHelper.getInstance().delete((long) tag);
		}
	}
}
