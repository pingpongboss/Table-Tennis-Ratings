package wei.mark.tabletennis;

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
}