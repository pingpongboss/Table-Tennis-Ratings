package wei.mark.tabletennis;

import wei.mark.tabletennis.util.Debuggable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ActivityPlayerList extends FragmentActivity implements Debuggable {
	TableTennisRatings app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (TableTennisRatings) getApplication();

		setContentView(R.layout.activity_player_list);

		if (savedInstanceState == null) {
			FragmentPlayerList fragment = new FragmentPlayerList();
			fragment.setArguments(getIntent().getExtras());

			getSupportFragmentManager().beginTransaction()
					.add(R.id.content, fragment).commit();
		}
	}

	@Override
	public void debug(String msg) {
//		if (mDebuggable) {
//			app.CurrentDebugMessage = app.CurrentDebugMessage + "\n" + msg;
//
//			debugTextView.post(new Runnable() {
//
//				@Override
//				public void run() {
//					debugTextView.setText(app.CurrentDebugMessage);
//
//					debugScrollView.post(new Runnable() {
//
//						@Override
//						public void run() {
//							debugScrollView.fullScroll(ScrollView.FOCUS_DOWN);
//						}
//					});
//				}
//			});
//		}
	}
}
