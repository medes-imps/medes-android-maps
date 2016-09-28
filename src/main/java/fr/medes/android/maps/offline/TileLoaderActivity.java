package fr.medes.android.maps.offline;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import fr.medes.android.maps.R;
import fr.medes.android.maps.offline.TileLoaderService.TileLoaderServiceListener;

public class TileLoaderActivity extends AppCompatActivity implements OnClickListener {

	private View mCancelButton;
	private View mCloseButton;

	private ProgressBar mProgressBar;
	private TextView mPercentView;
	private TextView mSizeView;

	private TileLoaderService mBoundService;
	private boolean mIsBound = false;

	private int mTilesToLoad = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.maps__tile_loader_content);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mCancelButton = findViewById(R.id.maps__offline_cancel);
		mCloseButton = findViewById(R.id.maps__offline_close);

		mProgressBar = (ProgressBar) findViewById(R.id.maps__offline_progress);
		mPercentView = (TextView) findViewById(R.id.maps__offline_percent);
		mSizeView = (TextView) findViewById(R.id.maps__offline_size);

		mCancelButton.setOnClickListener(this);
		mCloseButton.setOnClickListener(this);

		doBindService();
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
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	private void doBindService() {
		if (!mIsBound) {
			mIsBound = bindService(new Intent(this, TileLoaderService.class), mConnection, 0);
		}
	}

	private void doUnbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.maps__offline_cancel) {
			if (mBoundService != null) {
				mBoundService.stop();
			}
		} else if (v.getId() == R.id.maps__offline_close) {
			finish();
		}
	}

	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((TileLoaderService.TileLoaderBinder) service).getService();
			if (mBoundService != null) {
				mTilesToLoad = mBoundService.getTilesToDownloadCount();
				int loaded = mBoundService.getTilesDownloadedCount();

				mProgressBar.setMax(mTilesToLoad);
				mProgressBar.setProgress(loaded);

				mPercentView.setText(getString(R.string.maps__offline_percent, loaded * 100 / mTilesToLoad));
				mSizeView.setText(getString(R.string.maps__offline_size, loaded, mTilesToLoad));

				mBoundService.setTileLoaderServiceListener(mListener);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBoundService.setTileLoaderServiceListener(null);
			mBoundService = null;
			finish();
		}
	};

	private final TileLoaderServiceListener mListener = new TileLoaderServiceListener() {

		@Override
		public void updateLoadedTilesCount(final int loaded) {
			mProgressBar.setProgress(loaded);
			mPercentView.post(new Runnable() {
				@Override
				public void run() {
					mPercentView.setText(getString(R.string.maps__offline_percent, loaded * 100 / mTilesToLoad));
				}
			});
			mSizeView.post(new Runnable() {
				@Override
				public void run() {
					mSizeView.setText(getString(R.string.maps__offline_size, loaded, mTilesToLoad));
				}
			});
		}

		@Override
		public void finished() {
			finish();
		}

	};

}
