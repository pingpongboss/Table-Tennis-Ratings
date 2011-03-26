package wei.mark.tabletennis;

import wei.mark.tabletennis.TableTennisRatings.Navigation;
import wei.mark.tabletennis.util.Debuggable;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class ActivityPlayerList extends FragmentActivity implements Debuggable {
	boolean mDebuggable;

	TextView debugTextView;
	ScrollView debugScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_player_list);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}

		if (savedInstanceState == null) {
			FragmentPlayerList fragment = new FragmentPlayerList();
			fragment.setArguments(getIntent().getExtras());

			getSupportFragmentManager().beginTransaction()
					.add(R.id.content, fragment).commit();
		}

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
	public void onBackPressed() {
		super.onBackPressed();
		((TableTennisRatings) getApplication()).CurrentNavigation = Navigation.IDLE;
		debug("Current navigation is now "
				+ ((TableTennisRatings) getApplication()).CurrentNavigation
						.toString());
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
