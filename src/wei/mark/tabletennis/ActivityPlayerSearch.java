package wei.mark.tabletennis;

import wei.mark.tabletennis.TableTennisRatings.Navigation;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ActivityPlayerSearch extends FragmentActivity {
	TableTennisRatings app;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (TableTennisRatings) getApplication();

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
}