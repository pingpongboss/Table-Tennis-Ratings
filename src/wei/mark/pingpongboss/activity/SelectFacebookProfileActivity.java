package wei.mark.pingpongboss.activity;

import java.util.ArrayList;

import wei.mark.pingpongboss.Pingpongboss;
import wei.mark.pingpongboss.R;
import wei.mark.pingpongboss.misc.adapter.FriendModelAdapter;
import wei.mark.pingpongboss.misc.model.FriendModel;
import wei.mark.pingpongboss.misc.task.FriendsTask;
import wei.mark.pingpongboss.misc.task.FriendsTask.FriendsCallback;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SelectFacebookProfileActivity extends ListActivity implements
		FriendsCallback {
	ArrayList<FriendModel> mFriends;
	Pingpongboss app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_facebook_profile);

		app = (Pingpongboss) getApplication();

		EditText search = (EditText) findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				((FriendModelAdapter) getListAdapter()).getFilter().filter(s);
			}
		});

		mFriends = new ArrayList<FriendModel>();

		setListAdapter(new FriendModelAdapter(this,
				R.layout.item_player_friends, mFriends));
	}

	@Override
	protected void onResume() {
		super.onResume();

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				retrieveFriends();
			}
		};

		app.login(this, false, runnable);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (app.friendsTask != null) {
			app.friendsTask.setFriendsCallback(null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		app.facebook.authorizeCallback(requestCode, resultCode, data);
	}

	private void retrieveFriends() {
		if (app.facebookId == null) {
			fail("retriveFriends facebookId is null");
			return;
		}

		mFriends.clear();
		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();

		if (app.friendsTask == null) {
			app.friendsTask = new FriendsTask(this);
			app.friendsTask.execute(app.facebookId,
					app.facebook.getAccessToken(), "false");
		} else if (!app.facebookId.equals(app.friendsTask.getFacebookId())) {
			app.friendsTask.cancel(true);
			app.friendsTask = new FriendsTask(this);
			app.friendsTask.execute(app.facebookId,
					app.facebook.getAccessToken(), "false");
		}

		app.friendsTask.setFriendsCallback(this);
	}

	@Override
	public void friendsCompleted(ArrayList<FriendModel> friends) {
		app.friendsTask = null;

		mFriends.clear();
		if (friends != null && !friends.isEmpty()) {
			mFriends.addAll(friends);
			((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
		} else
			fail("friendsCompleted friends is null or empty");
	}

	private void fail(Object error) {
		setResult(RESULT_CANCELED);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		FriendModel profile = ((FriendModelAdapter) getListAdapter())
				.getFriendModel(position);
		Intent data = new Intent();
		data.putExtra("profile", profile);
		setResult(RESULT_OK, data);
		finish();
	}
}
