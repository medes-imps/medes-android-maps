package fr.medes.android.maps.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

public class BoundingBoxOverlay extends Overlay {

	private final Rect rect = new Rect();
	private final Point reuse = new Point();
	private final Paint paint = new Paint() {
		{
			setAntiAlias(false);
			setColor(Color.BLACK);
			setStrokeWidth(3);
			setStyle(Style.STROKE);
		}
	};

	private double mNorth;
	private double mEast;
	private double mSouth;
	private double mWest;

	public BoundingBoxOverlay() {
		super();
	}

	public void setBounds(double north, double east, double south, double west) {
		mNorth = north;
		mEast = east;
		mSouth = south;
		mWest = west;
	}

	public void show(MapView mapView) {
		mapView.postInvalidate();
	}

	@Override
	protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection pj = mapView.getProjection();
		pj.toPixels(new GeoPoint(mNorth, mEast), reuse);
		rect.top = reuse.y;
		rect.right = reuse.x;
		pj.toPixels(new GeoPoint(mSouth, mWest), reuse);
		rect.bottom = reuse.y;
		rect.left = reuse.x;
		canvas.drawRect(rect, paint);
	}

}
