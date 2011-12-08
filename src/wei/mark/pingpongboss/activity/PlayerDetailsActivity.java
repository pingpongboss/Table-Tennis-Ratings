package wei.mark.pingpongboss.activity;

import wei.mark.pingpongboss.PingPongBoss;
import wei.mark.pingpongboss.PingPongBoss.Navigation;
import wei.mark.pingpongboss.R;
import wei.mark.pingpongboss.fragment.PlayerDetailsFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PlayerDetailsActivity extends FragmentActivity {
	PingPongBoss app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getApplication();

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}

		setContentView(R.layout.activity_player_details);

		if (savedInstanceState == null) {
			PlayerDetailsFragment fragment = new PlayerDetailsFragment();
			fragment.setArguments(getIntent().getExtras());

			getSupportFragmentManager().beginTransaction()
					.add(R.id.content, fragment).commit();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		app.CurrentNavigation = Navigation.LIST;
		startActivity(new Intent().setClass(this, MainViewPagerActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

}
