package wei.mark.pingpongboss;

import wei.mark.pingpongboss.PingPongBoss.Navigation;
import wei.mark.pingpongboss.R;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ActivityDualPlayerList extends FragmentActivity {
	PingPongBoss app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getApplication();

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}

		setContentView(R.layout.activity_dual_player_list);

		if (savedInstanceState == null) {
			FragmentPlayerList usattFragment = new FragmentPlayerList();
			usattFragment.setArguments(getIntent().getExtras());
			usattFragment.getArguments().putString("provider", "usatt");
			FragmentPlayerList rcFragment = new FragmentPlayerList();
			rcFragment.setArguments(getIntent().getExtras());
			rcFragment.getArguments().putString("provider", "rc");

			getSupportFragmentManager().beginTransaction()
					.add(R.id.usatt, usattFragment).add(R.id.rc, rcFragment)
					.commit();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		app.CurrentNavigation = Navigation.IDLE;
		startActivity(new Intent().setClass(this, ActivityPlayerSearch.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}
}