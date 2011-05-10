package wei.mark.pingpongboss.deprecated;

import wei.mark.pingpongboss.PingPongBoss;
import wei.mark.pingpongboss.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TabHost;

@Deprecated
public class ActivityTabbedPlayerList extends TabActivity {
	PingPongBoss app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getApplication();

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
