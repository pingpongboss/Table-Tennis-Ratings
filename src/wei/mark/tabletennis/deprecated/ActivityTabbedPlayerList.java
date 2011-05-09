package wei.mark.tabletennis.deprecated;

import wei.mark.tabletennis.R;
import wei.mark.tabletennis.TableTennisRatings;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TabHost;

@Deprecated
public class ActivityTabbedPlayerList extends TabActivity {
	TableTennisRatings app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (TableTennisRatings) getApplication();

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}

		setContentView(R.layout.activity_tabbed_player_list);

		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, ActivityPlayerList.class)
				.putExtras(getIntent().getExtras())
				.putExtra("provider", "usatt");
		spec = tabHost
				.newTabSpec("usatt")
				.setIndicator(null,
						getResources().getDrawable(R.drawable.usatt_selector))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ActivityPlayerList.class)
				.putExtras(getIntent().getExtras()).putExtra("provider", "rc");
		spec = tabHost
				.newTabSpec("rc")
				.setIndicator(null,
						getResources().getDrawable(R.drawable.rc_selector))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}
}
