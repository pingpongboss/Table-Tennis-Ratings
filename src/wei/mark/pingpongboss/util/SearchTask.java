package wei.mark.pingpongboss.util;

import java.util.ArrayList;

import wei.mark.pingpongboss.model.PlayerModel;
import android.os.AsyncTask;

public class SearchTask extends AsyncTask<String, Void, ArrayList<PlayerModel>> {
	SearchCallback callback;
	String provider, query, id;
	boolean user, fresh, hasSavedResult;
	ArrayList<PlayerModel> savedResult;

	public SearchTask(SearchCallback searchCallback) {
		callback = searchCallback;
	}

	@Override
	protected ArrayList<PlayerModel> doInBackground(String... params) {
		try {
			id = params[0];
			provider = params[1];
			query = params[2];
			user = Boolean.parseBoolean(params[3]);
			fresh = Boolean.parseBoolean(params[4]);

			AppEngineParser parser = AppEngineParser.getParser();

			return parser.search(id, provider, query, fresh);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(ArrayList<PlayerModel> result) {

		if (callback != null) {
			callback.searchCompleted(result);
		} else {
			hasSavedResult = true;
			savedResult = result;
		}
	}

	public void setSearchCallback(SearchCallback searchCallback) {
		callback = searchCallback;

		if (callback != null && hasSavedResult) {
			callback.searchCompleted(savedResult);

			hasSavedResult = false;
			savedResult = null;
		}
	}

	public String getProvider() {
		return provider;
	}

	public String getQuery() {
		return query;
	}

	public interface SearchCallback {
		void searchCompleted(ArrayList<PlayerModel> players);
	}

}
