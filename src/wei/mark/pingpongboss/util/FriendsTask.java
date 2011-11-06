package wei.mark.pingpongboss.util;

import java.util.ArrayList;

import wei.mark.pingpongboss.model.FriendModel;
import android.os.AsyncTask;

public class FriendsTask extends
		AsyncTask<String, Void, ArrayList<FriendModel>> {
	FriendsCallback callback;
	String facebookId, accessToken;
	boolean hasSavedResult;
	ArrayList<FriendModel> savedResult;

	public FriendsTask(FriendsCallback friendsCallback) {
		callback = friendsCallback;
	}

	@Override
	protected ArrayList<FriendModel> doInBackground(String... params) {
		try {
			facebookId = params[0];
			accessToken = params[1];

			return AppEngineParser.getParser().friends(facebookId, accessToken);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(ArrayList<FriendModel> result) {
		if (callback != null) {
			callback.friendsCompleted(result);
		} else {
			hasSavedResult = true;
			savedResult = result;
		}
	}

	public void setFriendsCallback(FriendsCallback friendsCallback) {
		callback = friendsCallback;

		if (callback != null && hasSavedResult) {
			callback.friendsCompleted(savedResult);

			hasSavedResult = false;
			savedResult = null;
		}
	}

	public String getFacebookId() {
		return facebookId;
	}

	public interface FriendsCallback {
		void friendsCompleted(ArrayList<FriendModel> friends);
	}
}
