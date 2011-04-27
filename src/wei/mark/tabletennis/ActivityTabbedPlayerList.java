package wei.mark.tabletennis;

import wei.mark.tabletennis.TableTennisRatings.ListNavigation;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

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

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if ("usatt".equals(tabId))
					app.CurrentListNavigation = ListNavigation.USATT;
				else if ("rc".equals(tabId))
					app.CurrentListNavigation = ListNavigation.RC;
			}
		});

		switch (app.CurrentListNavigation) {
		case RC:
			tabHost.setCurrentTab(1);
			break;
		case USATT:
		default:
			tabHost.setCurrentTab(0);
			break;
		}
	}
}
