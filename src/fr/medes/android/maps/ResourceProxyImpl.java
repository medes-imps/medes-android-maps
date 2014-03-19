package fr.medes.android.maps;

import org.osmdroid.ResourceProxy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import fr.medes.android.maps.R;

public class ResourceProxyImpl implements ResourceProxy {

	private final Resources mResources;

	public ResourceProxyImpl(Context context) {
		mResources = context.getResources();
	}

	@Override
	public Bitmap getBitmap(bitmap resId) {
		switch (resId) {
		case center:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__center);
		case direction_arrow:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__direction_arrow);
		case ic_menu_compass:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__ic_menu_compass);
		case ic_menu_mapmode:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__ic_menu_mapmode);
		case ic_menu_mylocation:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__ic_menu_mylocation);
		case ic_menu_offline:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__ic_menu_offline);
		case marker_default:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__marker_default);
		case marker_default_focused_base:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__marker_default_focused_base);
		case navto_small:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__navto_small);
		case next:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__next);
		case person:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__person);
		case previous:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__previous);
		case unknown:
			return BitmapFactory.decodeResource(mResources, R.drawable.maps__center);
		default:
			return null;
		}
	}

	@Override
	public float getDisplayMetricsDensity() {
		return mResources.getDisplayMetrics().density;
	}

	@Override
	public Drawable getDrawable(bitmap resId) {
		switch (resId) {
		case center:
			return mResources.getDrawable(R.drawable.maps__center);
		case direction_arrow:
			return mResources.getDrawable(R.drawable.maps__direction_arrow);
		case ic_menu_compass:
			return mResources.getDrawable(R.drawable.maps__ic_menu_compass);
		case ic_menu_mapmode:
			return mResources.getDrawable(R.drawable.maps__ic_menu_mapmode);
		case ic_menu_mylocation:
			return mResources.getDrawable(R.drawable.maps__ic_menu_mylocation);
		case ic_menu_offline:
			return mResources.getDrawable(R.drawable.maps__ic_menu_offline);
		case marker_default:
			return mResources.getDrawable(R.drawable.maps__marker_default);
		case marker_default_focused_base:
			return mResources.getDrawable(R.drawable.maps__marker_default_focused_base);
		case navto_small:
			return mResources.getDrawable(R.drawable.maps__navto_small);
		case next:
			return mResources.getDrawable(R.drawable.maps__next);
		case person:
			return mResources.getDrawable(R.drawable.maps__person);
		case previous:
			return mResources.getDrawable(R.drawable.maps__previous);
		case unknown:
			return mResources.getDrawable(R.drawable.maps__center);
		default:
			return null;
		}
	}

	@Override
	public String getString(string resId) {
		switch (resId) {
		case base:
			return mResources.getString(R.string.maps__base);
		case base_nl:
			return mResources.getString(R.string.maps__base_nl);
		case bing:
			return mResources.getString(R.string.maps__bing);
		case cloudmade_small:
			return mResources.getString(R.string.maps__cloudmade_small);
		case cloudmade_standard:
			return mResources.getString(R.string.maps__cloudmade_standard);
		case compass:
			return mResources.getString(R.string.maps__compass);
		case cyclemap:
			return mResources.getString(R.string.maps__cyclemap);
		case fiets_nl:
			return mResources.getString(R.string.maps__fiets_nl);
		case format_distance_feet:
			return mResources.getString(R.string.maps__format_distance_feet);
		case format_distance_kilometers:
			return mResources.getString(R.string.maps__format_distance_kilometers);
		case format_distance_meters:
			return mResources.getString(R.string.maps__format_distance_meters);
		case format_distance_miles:
			return mResources.getString(R.string.maps__format_distance_miles);
		case format_distance_nautical_miles:
			return mResources.getString(R.string.maps__format_distance_nautical_miles);
		case hills:
			return mResources.getString(R.string.maps__hills);
		case map_mode:
			return mResources.getString(R.string.maps__map_mode);
		case mapnik:
			return mResources.getString(R.string.maps__mapnik);
		case mapquest_aerial:
			return mResources.getString(R.string.maps__mapquest_aerial);
		case mapquest_osm:
			return mResources.getString(R.string.maps__mapquest_osm);
		case my_location:
			return mResources.getString(R.string.maps__my_location);
		case offline_mode:
			return mResources.getString(R.string.maps__offline_mode);
		case online_mode:
			return mResources.getString(R.string.maps__online_mode);
		case public_transport:
			return mResources.getString(R.string.maps__public_transport);
		case roads_nl:
			return mResources.getString(R.string.maps__roads_nl);
		case topo:
			return mResources.getString(R.string.maps__topo);
		case unknown:
			return mResources.getString(R.string.maps__unknown);
		default:
			return null;
		}
	};

	@Override
	public String getString(string resId, Object... formatArgs) {
		switch (resId) {
		case base:
			return mResources.getString(R.string.maps__base, formatArgs);
		case base_nl:
			return mResources.getString(R.string.maps__base_nl, formatArgs);
		case bing:
			return mResources.getString(R.string.maps__bing, formatArgs);
		case cloudmade_small:
			return mResources.getString(R.string.maps__cloudmade_small, formatArgs);
		case cloudmade_standard:
			return mResources.getString(R.string.maps__cloudmade_standard, formatArgs);
		case compass:
			return mResources.getString(R.string.maps__compass, formatArgs);
		case cyclemap:
			return mResources.getString(R.string.maps__cyclemap, formatArgs);
		case fiets_nl:
			return mResources.getString(R.string.maps__fiets_nl, formatArgs);
		case format_distance_feet:
			return mResources.getString(R.string.maps__format_distance_feet, formatArgs);
		case format_distance_kilometers:
			return mResources.getString(R.string.maps__format_distance_kilometers, formatArgs);
		case format_distance_meters:
			return mResources.getString(R.string.maps__format_distance_meters, formatArgs);
		case format_distance_miles:
			return mResources.getString(R.string.maps__format_distance_miles, formatArgs);
		case format_distance_nautical_miles:
			return mResources.getString(R.string.maps__format_distance_nautical_miles, formatArgs);
		case hills:
			return mResources.getString(R.string.maps__hills, formatArgs);
		case map_mode:
			return mResources.getString(R.string.maps__map_mode, formatArgs);
		case mapnik:
			return mResources.getString(R.string.maps__mapnik, formatArgs);
		case mapquest_aerial:
			return mResources.getString(R.string.maps__mapquest_aerial, formatArgs);
		case mapquest_osm:
			return mResources.getString(R.string.maps__mapquest_osm, formatArgs);
		case my_location:
			return mResources.getString(R.string.maps__my_location, formatArgs);
		case offline_mode:
			return mResources.getString(R.string.maps__offline_mode, formatArgs);
		case online_mode:
			return mResources.getString(R.string.maps__online_mode, formatArgs);
		case public_transport:
			return mResources.getString(R.string.maps__public_transport, formatArgs);
		case roads_nl:
			return mResources.getString(R.string.maps__roads_nl, formatArgs);
		case topo:
			return mResources.getString(R.string.maps__topo, formatArgs);
		case unknown:
			return mResources.getString(R.string.maps__unknown, formatArgs);
		default:
			return null;
		}
	}

}
