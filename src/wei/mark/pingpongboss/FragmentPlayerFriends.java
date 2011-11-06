package wei.mark.pingpongboss;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wei.mark.pingpongboss.model.FriendModel;
import wei.mark.pingpongboss.util.Constants;
import wei.mark.pingpongboss.util.FriendModelAdapter;
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
import com.facebook.android.Util;

public class FragmentPlayerFriends extends ListFragment {
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

	private void retrieveFriends() {
		new AsyncFacebookRunner(app.facebook).request(
				Constants.GRAPH_PATH_FRIENDS, new RequestListener() {

					@Override
					public void onMalformedURLException(
							MalformedURLException e, Object state) {
					}

					@Override
					public void onIOException(IOException e, Object state) {
					}

					@Override
					public void onFileNotFoundException(
							FileNotFoundException e, Object state) {
					}

					@Override
					public void onFacebookError(FacebookError e, Object state) {
					}

					@Override
					public void onComplete(String response, Object state) {
						try {
							JSONObject data = Util.parseJson(response);

							mFriends.clear();

							JSONArray friends = data.getJSONArray("data");
							for (int i = 0; i < friends.length(); i++) {
								final JSONObject friend = friends
										.getJSONObject(i);
								mFriends.add(new FriendModel(friend
										.getString("id"), friend
										.getString("name")));
							}

							FragmentPlayerFriends.this.getActivity()
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											((ArrayAdapter<?>) getListAdapter())
													.notifyDataSetChanged();
										}
									});

						} catch (JSONException e) {
							e.printStackTrace();
						} catch (FacebookError e) {
							if (e.getErrorType() == "OAuthException") {
								facebookAuthorize();
							}
						}
					}
				});
	}

	private void facebookAuthorize() {
		facebookPrefs = getActivity().getSharedPreferences("facebook",
				Context.MODE_PRIVATE);
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
		if (app.facebook.isSessionValid()) {
			retrieveFriends();
		} else {
			app.facebook.authorize(getActivity(), new String[] {},
					new DialogListener() {
						@Override
						public void onComplete(Bundle values) {
							SharedPreferences.Editor editor = facebookPrefs
									.edit();
							editor.putString("access_token",
									app.facebook.getAccessToken());
							editor.putLong("access_expires",
									app.facebook.getAccessExpires());
							editor.commit();

							retrieveFriends();
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
