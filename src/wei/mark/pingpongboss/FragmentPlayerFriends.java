package wei.mark.pingpongboss;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONObject;

import wei.mark.pingpongboss.model.FriendModel;
import wei.mark.pingpongboss.util.FriendModelAdapter;
import wei.mark.pingpongboss.util.FriendsTask;
import wei.mark.pingpongboss.util.FriendsTask.FriendsCallback;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FragmentPlayerFriends extends ListFragment implements
		FriendsCallback {
	PingPongBoss app;
	SharedPreferences facebookPrefs;

	ArrayList<FriendModel> mFriends;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (PingPongBoss) getActivity().getApplication();

		mFriends = new ArrayList<FriendModel>();

		setListAdapter(new FriendModelAdapter(getActivity(),
				R.layout.item_player_friends, mFriends));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_player_friends, null,
				false);

		Button login = (Button) view.findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				facebookAuthorize();
			}
		});

		return view;
	}

	@Override
	public void onDestroy() {
		((FriendModelAdapter) getListAdapter()).getLoader().clearCache();

		super.onDestroy();
	}

	private void retrieveFriends() {
		if (app.facebookId == null)
			return;

		mFriends.clear();
		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();

		if (app.friendsTask == null) {
			app.friendsTask = new FriendsTask(this);
			app.friendsTask.execute(app.facebookId,
					app.facebook.getAccessToken());
		} else if (!app.facebookId.equals(app.friendsTask.getFacebookId())) {
			app.friendsTask.cancel(true);
			app.friendsTask = new FriendsTask(this);
			app.friendsTask.execute(app.facebookId,
					app.facebook.getAccessToken());
		}

		app.friendsTask.setFriendsCallback(this);
	}

	@Override
	public void friendsCompleted(ArrayList<FriendModel> friends) {
		app.friendsTask = null;
		
		mFriends.clear();
		if (friends != null)
			mFriends.addAll(friends);
		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}

	private void facebookAuthorize() {
		facebookPrefs = getActivity().getSharedPreferences("facebook",
				Context.MODE_PRIVATE);
		String access_token = facebookPrefs.getString("access_token", null);
		long expires = facebookPrefs.getLong("access_expires", 0);
		app.facebookId = facebookPrefs.getString("facebookId", null);
		if (access_token != null) {
			app.facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			app.facebook.setAccessExpires(expires);
		}

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if (app.facebook.isSessionValid() && app.facebookId != null) {
			retrieveFriends();
		} else {
			app.facebook.authorize(getActivity(), new String[] {},
					new DialogListener() {
						@Override
						public void onComplete(Bundle values) {
							new AsyncFacebookRunner(app.facebook).request("me",
									new RequestListener() {

										@Override
										public void onMalformedURLException(
												MalformedURLException e,
												Object state) {
										}

										@Override
										public void onIOException(
												IOException e, Object state) {
										}

										@Override
										public void onFileNotFoundException(
												FileNotFoundException e,
												Object state) {
										}

										@Override
										public void onFacebookError(
												FacebookError e, Object state) {
										}

										@Override
										public void onComplete(String response,
												Object state) {
											JSONObject me;
											try {
												me = new JSONObject(response);
												app.facebookId = me
														.getString("id");

												SharedPreferences.Editor editor = facebookPrefs
														.edit();
												editor.putString(
														"access_token",
														app.facebook
																.getAccessToken());
												editor.putLong(
														"access_expires",
														app.facebook
																.getAccessExpires());
												editor.putString("facebookId",
														app.facebookId);
												editor.commit();

												retrieveFriends();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
						}

						@Override
						public void onFacebookError(FacebookError error) {
							return;
						}

						@Override
						public void onError(DialogError e) {
							return;
						}

						@Override
						public void onCancel() {
							return;
						}
					});
		}
	}
}
