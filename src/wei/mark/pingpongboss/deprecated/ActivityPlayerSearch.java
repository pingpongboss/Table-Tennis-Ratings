package wei.mark.pingpongboss.deprecated;

import wei.mark.pingpongboss.PingPongBoss;
import wei.mark.pingpongboss.PingPongBoss.Navigation;
import wei.mark.pingpongboss.R;
import wei.mark.pingpongboss.fragment.PlayerDetailsFragment;
import wei.mark.pingpongboss.fragment.PlayerListFragment;
import wei.mark.pingpongboss.misc.model.Refreshable;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

@Deprecated
public class ActivityPlayerSearch extends FragmentActivity implements
		Refreshable {
	PingPongBoss app;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getApplication();

		setContentView(R.layout.activity_player_search);
	}

	@Override
	public void onBackPressed() {
		if (app.DualPane && app.CurrentNavigation == Navigation.DETAILS) {
			app.CurrentNavigation = Navigation.LIST;
			startActivity(new Intent().setClass(this,
					ActivityPlayerSearch.class).setFlags(
					Intent.FLAG_ACTIVITY_CLEAR_TOP));
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void refresh() {
		PlayerDetailsFragment detailsFragment = (PlayerDetailsFragment) getSupportFragmentManager()
				.findFragmentByTag("details");
		if (detailsFragment != null) {
			detailsFragment.fetchDetails(true);
			return;
		}

		PlayerListFragment usattFragment = (PlayerListFragment) getSupportFragmentManager()
				.findFragmentByTag("usatt");
		PlayerListFragment rcFragment = (PlayerListFragment) getSupportFragmentManager()
				.findFragmentByTag("rc");
		if (usattFragment != null)
			usattFragment.startSearch(true);
		if (rcFragment != null)
			rcFragment.startSearch(true);
		return;
	}
}