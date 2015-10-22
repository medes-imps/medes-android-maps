package fr.medes.android.maps.overlay;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

public class BubbleClickableOverlay extends BubbleOverlay {

	private static final float ACCURACY = 10f;

	private boolean mWasSelected;

	private float firstX;
	private float firstY;

	private int dx;
	private int dy;

	private final LongClickRunnable mRunnable = new LongClickRunnable();

	public BubbleClickableOverlay(Context context, GeoPoint point) {
		super(context, point);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		Projection pj = mapView.getProjection();
		final float x = event.getX();
		final float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				firstX = x;
				firstY = y;
				mWasSelected = overlayIsSelected((int) x, (int) y);
				if (!mWasSelected) {
					dx = dy = 0;
					IGeoPoint point = pj.fromPixels((int) x, (int) y);
					mRunnable.setGeoPoint(point);
					mapView.postDelayed(mRunnable, ViewConfiguration.getLongPressTimeout());
				} else {
					Point marker = getMarkerPoint();
					dx = (int) x - marker.x;
					dy = (int) y - marker.y;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mWasSelected) {
					setGeoPoint(pj.fromPixels((int) x - dx, (int) y - dy));
				} else if (Math.hypot(x - firstX, y - firstY) > ACCURACY) {
					mapView.removeCallbacks(mRunnable);
				}
				break;
			default:
				mWasSelected = false;
				mapView.removeCallbacks(mRunnable);
				break;
		}
		return mWasSelected;
	}

	private class LongClickRunnable implements Runnable {

		private IGeoPoint mPoint;

		public void setGeoPoint(IGeoPoint point) {
			mPoint = point;
		}

		@Override
		public void run() {
			setGeoPoint(mPoint);
			mWasSelected = true;
		}
	}

}