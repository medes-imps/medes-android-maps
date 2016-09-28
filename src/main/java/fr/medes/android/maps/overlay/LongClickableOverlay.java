package fr.medes.android.maps.overlay;

import android.graphics.Canvas;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

public class LongClickableOverlay extends Overlay {

	public interface OnLongClickListener {
		boolean onLongClick(MapView mapView, IGeoPoint point);
	}

	private OnLongClickListener mListener;

	public LongClickableOverlay(OnLongClickListener listener) {
		super();
		mListener = listener;
	}

	public void setOnLongClickListener(OnLongClickListener listener) {
		mListener = listener;
	}

	@Override
	protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// nothing to draw
	}

	@Override
	public boolean onLongPress(MotionEvent e, MapView mapView) {
		if (mListener != null) {
			final IGeoPoint point = mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());
			return mListener.onLongClick(mapView, point);
		}
		return super.onLongPress(e, mapView);
	}

}
