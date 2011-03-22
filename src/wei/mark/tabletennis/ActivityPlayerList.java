package wei.mark.tabletennis;

import wei.mark.tabletennis.TableTennisRatings.Navigation;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ActivityPlayerList extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_player_list);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}

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
		((TableTennisRatings) getApplication()).CurrentNavigation = Navigation.SEARCH;
	}
}
