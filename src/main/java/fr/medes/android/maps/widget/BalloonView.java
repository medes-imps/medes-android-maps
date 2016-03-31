package fr.medes.android.maps.widget;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import fr.medes.android.maps.R;

public class BalloonView extends FrameLayout {

	private final TextView title;
	private final TextView readMore;
	private final TextView snippet;

	/**
	 * Create a new BalloonOverlayView.
	 *
	 * @param context The activity context.
	 */
	public BalloonView(final Context context) {
		super(context);
		inflate(context, R.layout.maps__balloon_content, this);

		setPadding(10, 0, 10, 0);

		title = (TextView) findViewById(R.id.maps__balloon_item_title);
		snippet = (TextView) findViewById(R.id.maps__balloon_item_snippet);
		readMore = (TextView) findViewById(R.id.maps__balloon_item_readmore);

		readMore.setText("Read more..");

		ImageView close = (ImageView) findViewById(R.id.maps__close_img_button);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setVisibility(GONE);
			}
		});

	}

	/**
	 * Set the title and description of the view
	 *
	 * @param strTitle title
	 * @param strDesc  description
	 */
	public void setData(String strTitle, String strDesc) {
		setVisibility(VISIBLE);
		if (strTitle != null) {
			title.setVisibility(VISIBLE);
			title.setText(strTitle);
		} else {
			title.setVisibility(GONE);
		}
		if (strDesc != null) {
			snippet.setVisibility(VISIBLE);
			snippet.setText(strDesc);
		} else {
			snippet.setVisibility(GONE);
		}
	}

}
