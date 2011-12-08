package wei.mark.pingpongboss.activity;

import wei.mark.pingpongboss.PingPongBoss;
import wei.mark.pingpongboss.PingPongBoss.Navigation;
import wei.mark.pingpongboss.R;
import wei.mark.pingpongboss.fragment.PlayerListFragment;
import wei.mark.pingpongboss.misc.model.Refreshable;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DualPlayerListActivity extends FragmentActivity implements
		Refreshable {
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
			PlayerListFragment usattFragment = new PlayerListFragment();
			usattFragment.setArguments(getIntent().getExtras());
			usattFragment.getArguments().putString("provider", "usatt");
			PlayerListFragment rcFragment = new PlayerListFragment();
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
		startActivity(new Intent().setClass(this, MainViewPagerActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	@Override
	public void refresh() {
		PlayerListFragment usattFragment = (PlayerListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.usatt);
		PlayerListFragment rcFragment = (PlayerListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.rc);
		if (usattFragment != null)
			usattFragment.startSearch(true);
		if (rcFragment != null)
			rcFragment.startSearch(true);
	}
}
