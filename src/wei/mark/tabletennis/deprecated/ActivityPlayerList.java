package wei.mark.tabletennis.deprecated;

import wei.mark.tabletennis.ActivityPlayerSearch;
import wei.mark.tabletennis.FragmentPlayerList;
import wei.mark.tabletennis.R;
import wei.mark.tabletennis.PingPongBoss;
import wei.mark.tabletennis.PingPongBoss.Navigation;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

@Deprecated
public class ActivityPlayerList extends FragmentActivity {
	PingPongBoss app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getApplication();

		setContentView(R.layout.activity_player_list);

		if (savedInstanceState == null) {
			FragmentPlayerList fragment = new FragmentPlayerList();
			fragment.setArguments(getIntent().getExtras());

			getSupportFragmentManager().beginTransaction()
					.add(R.id.content, fragment).commit();
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
