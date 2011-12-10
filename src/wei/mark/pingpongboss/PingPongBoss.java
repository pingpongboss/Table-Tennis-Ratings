package wei.mark.pingpongboss;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import wei.mark.pingpongboss.misc.model.PlayerModel;
import wei.mark.pingpongboss.misc.task.DetailsTask;
import wei.mark.pingpongboss.misc.task.FriendsTask;
import wei.mark.pingpongboss.misc.task.SearchTask;
import wei.mark.pingpongboss.util.FileUtils;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class Pingpongboss extends Application {
	public Facebook facebook;
	public String facebookId;

	public Navigation CurrentNavigation;
	public int CurrentMainViewPagerPosition;

	public PlayerModel CurrentPlayerModel;
	public boolean DualPane;

	public SearchTask usattSearchTask, rcSearchTask;
	public DetailsTask detailsTask;
	public FriendsTask friendsTask;

	public enum Navigation {
		IDLE, LIST, DETAILS
	}

	@Override
	public void onCreate() {
		super.onCreate();

		facebook = new Facebook(getResources().getString(R.string.fb_app_id));

		getSharedPreferences("usatt", 0).edit().clear().commit();
		getSharedPreferences("rc", 0).edit().clear().commit();
		getSharedPreferences("search", 0).edit().clear().commit();

		CurrentNavigation = Navigation.IDLE;
		CurrentMainViewPagerPosition = 0;

		CurrentPlayerModel = null;
	}

	public String getDeviceId() {
		return FileUtils.id(this);
	}

	public void login(final Activity activity, boolean cachedOnly,
			final Runnable onCompleteRunnable, final Runnable onFailRunnable) {
		SharedPreferences facebookPrefs = activity.getSharedPreferences(
				"facebook", Context.MODE_PRIVATE);
		String access_token = facebookPrefs.getString("access_token", null);
		long expires = facebookPrefs.getLong("access_expires", 0);
		facebookId = facebookPrefs.getString("facebookId", null);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if (facebook.isSessionValid() && facebookId != null) {
			activity.runOnUiThread(onCompleteRunnable);
			return;
		}

		// cached login failed
		if (cachedOnly) {
			activity.runOnUiThread(onFailRunnable);
			return;
		}

		// login online
		facebook.authorize(activity, new String[] {}, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {
				new AsyncFacebookRunner(facebook).request("me",
						new RequestListener() {

							@Override
							public void onMalformedURLException(
									MalformedURLException e, Object state) {
								activity.runOnUiThread(onFailRunnable);
							}

							@Override
							public void onIOException(IOException e,
									Object state) {
								activity.runOnUiThread(onFailRunnable);
							}

							@Override
							public void onFileNotFoundException(
									FileNotFoundException e, Object state) {
								activity.runOnUiThread(onFailRunnable);
							}

							@Override
							public void onFacebookError(FacebookError e,
									Object state) {
								activity.runOnUiThread(onFailRunnable);
							}

							@Override
							public void onComplete(final String response,
									Object state) {
								JSONObject me;
								try {
									me = new JSONObject(response);

									facebookId = me.getString("id");
								} catch (JSONException e1) {
									e1.printStackTrace();
									activity.runOnUiThread(onFailRunnable);
									return;
								}

								SharedPreferences facebookPrefs = getSharedPreferences(
										"facebook", Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = facebookPrefs
										.edit();
								editor.putString("access_token",
										facebook.getAccessToken());
								editor.putLong("access_expires",
										facebook.getAccessExpires());
								editor.putString("facebookId", facebookId);
								editor.commit();

								activity.runOnUiThread(onCompleteRunnable);
							}
						});
			}

			@Override
			public void onFacebookError(FacebookError error) {
				activity.runOnUiThread(onFailRunnable);
			}

			@Override
			public void onError(DialogError e) {
				activity.runOnUiThread(onFailRunnable);
			}

			@Override
			public void onCancel() {
				activity.runOnUiThread(onFailRunnable);
			}
		});
	}

	public static Toast getToast(Context context, int imageResourceId,
			String message) {
		View view = getToastView(context, imageResourceId, message);

		Toast toast = new Toast(context);
		toast.setView(view);
		return toast;
	}

	public static View getToastView(Context context, int imageResourceId,
			String message) {
		LayoutInflater inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflator.inflate(R.layout.toast, null);

		ImageView image = (ImageView) view.findViewById(R.id.image);
		TextView text = (TextView) view.findViewById(R.id.text);

		if (imageResourceId != 0) {
			image.setImageResource(imageResourceId);
			image.setPadding(0, 0, 10, 0);
		} else {
			image.setPadding(0, 0, 0, 0);
		}

		if (message != null && !message.equals(""))
			text.setText(message);

		return view;
	}
}
