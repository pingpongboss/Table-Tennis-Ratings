package wei.mark.tabletennis;

import wei.mark.tabletennis.TableTennisRatings.Navigation;
import wei.mark.tabletennis.util.Debuggable;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class ActivityPlayerSearch extends FragmentActivity implements
		Debuggable {
	TableTennisRatings app;
	boolean mDebuggable;

	TextView debugTextView;
	ScrollView debugScrollView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (TableTennisRatings) getApplication();

		setContentView(R.layout.activity_player_search);

		mDebuggable = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		mDebuggable = false;
		if (mDebuggable) {
			findViewById(R.id.debug_stub).setVisibility(View.VISIBLE);
			debugTextView = (TextView) findViewById(R.id.debug);
			debugScrollView = (ScrollView) findViewById(R.id.debug_scroll);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mDebuggable) {
			debugTextView.setText(app.CurrentDebugMessage);
			debugScrollView.post(new Runnable() {

				@Override
				public void run() {
					debugScrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		int stackCount = getSupportFragmentManager().getBackStackEntryCount();
		if (stackCount == 0) {
			try {
				((FragmentPlayerSearch) getSupportFragmentManager()
						.findFragmentById(R.id.player_search)).clearQuery();
			} catch (Exception ex) {
				debug(ex.getMessage());
			}
			app.CurrentNavigation = Navigation.IDLE;
			debug("Current navigation is now "
					+ app.CurrentNavigation.toString());
		}
	}

	@Override
	public void debug(String msg) {
		if (mDebuggable) {
			app.CurrentDebugMessage = app.CurrentDebugMessage + "\n" + msg;

			debugTextView.post(new Runnable() {

				@Override
				public void run() {
					debugTextView.setText(app.CurrentDebugMessage);

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