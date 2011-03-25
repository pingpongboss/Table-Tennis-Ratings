package wei.mark.tabletennis;

import wei.mark.tabletennis.util.Debuggable;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class ActivityPlayerSearch extends FragmentActivity implements
		Debuggable {
	boolean mDebuggable;

	TextView debugTextView;
	ScrollView debugScrollView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_search);

		mDebuggable = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		if (mDebuggable) {
			findViewById(R.id.debug_stub).setVisibility(View.VISIBLE);
			debugTextView = (TextView) findViewById(R.id.debug);
			debugScrollView = (ScrollView) findViewById(R.id.debug_scroll);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		debugTextView
				.setText(((TableTennisRatings) getApplication()).CurrentDebugMessage);
		debugScrollView.post(new Runnable() {

			@Override
			public void run() {
				debugScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	@Override
	public void debug(String msg) {
		if (mDebuggable) {
			((TableTennisRatings) getApplication()).CurrentDebugMessage = ((TableTennisRatings) getApplication()).CurrentDebugMessage
					+ "\n" + msg;

			debugTextView.post(new Runnable() {

				@Override
				public void run() {
					debugTextView
							.setText(((TableTennisRatings) getApplication()).CurrentDebugMessage);

					debugScrollView.post(new Runnable() {

						@Override
						public void run() {
							debugScrollView.fullScroll(ScrollView.FOCUS_DOWN);
						}
					});
				}
			});
		}
	}
}