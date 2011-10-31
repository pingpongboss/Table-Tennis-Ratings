package wei.mark.pingpongboss;

import wei.mark.pingpongboss.PingPongBoss.Navigation;
import wei.mark.pingpongboss.model.Refreshable;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class ActivityPlayerSearch extends FragmentActivity implements
		Refreshable {
	PingPongBoss app;
	SharedPreferences facebookPrefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getApplication();

		setContentView(R.layout.activity_player_search);

		facebookPrefs = getSharedPreferences("facebook", MODE_PRIVATE);
		String access_token = facebookPrefs.getString("access_token", null);
		long expires = facebookPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			app.facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			app.facebook.setAccessExpires(expires);
		}

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if (!app.facebook.isSessionValid()) {

			app.facebook.authorize(this, new String[] {}, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					SharedPreferences.Editor editor = facebookPrefs.edit();
					editor.putString("access_token",
							app.facebook.getAccessToken());
					editor.putLong("access_expires",
							app.facebook.getAccessExpires());
					editor.commit();
				}

				@Override
				public void onFacebookError(FacebookError error) {
				}

				@Override
				public void onError(DialogError e) {
				}

				@Override
				public void onCancel() {
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		app.facebook.authorizeCallback(requestCode, resultCode, data);
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
		FragmentPlayerDetails detailsFragment = (FragmentPlayerDetails) getSupportFragmentManager()
				.findFragmentByTag("details");
		if (detailsFragment != null) {
			detailsFragment.fetchDetails(true);
			return;
		}

		FragmentPlayerList usattFragment = (FragmentPlayerList) getSupportFragmentManager()
				.findFragmentByTag("usatt");
		FragmentPlayerList rcFragment = (FragmentPlayerList) getSupportFragmentManager()
				.findFragmentByTag("rc");
		if (usattFragment != null)
			usattFragment.startSearch(true);
		if (rcFragment != null)
			rcFragment.startSearch(true);
		return;
	}
}