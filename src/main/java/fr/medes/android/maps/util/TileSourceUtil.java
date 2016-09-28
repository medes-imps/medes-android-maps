package fr.medes.android.maps.util;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.List;

public class TileSourceUtil {

	public static CharSequence[] readableTileSources() {
		final List<ITileSource> list = TileSourceFactory.getTileSources();
		final int size = list.size();
		CharSequence[] result = new CharSequence[size];
		for (int i = 0; i < size; i++) {
			result[i] = list.get(i).name();
		}
		return result;
	}

}
