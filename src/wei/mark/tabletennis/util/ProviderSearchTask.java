package wei.mark.tabletennis.util;

import java.util.ArrayList;

import wei.mark.tabletennis.TableTennisRatings;
import wei.mark.tabletennis.TableTennisRatings.Navigation;
import wei.mark.tabletennis.model.PlayerModel;
import android.os.AsyncTask;

public class ProviderSearchTask extends
		AsyncTask<String, Void, ArrayList<PlayerModel>> {
	SearchCallback callback;
	String provider, query, id;
	boolean user;

	public ProviderSearchTask(SearchCallback searchCallback) {
		callback = searchCallback;
	}

	public void setSearchCallback(SearchCallback searchCallback) {
		callback = searchCallback;
	}

	@Override
	protected ArrayList<PlayerModel> doInBackground(String... params) {
		try {
			id = params[0];
			provider = params[1];
			query = params[2];
			user = Boolean.parseBoolean(params[3]);

			AppEngineParser parser = AppEngineParser.getParser();

			return parser.execute(id, provider, query);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(ArrayList<PlayerModel> result) {
		callback.searchCompleted(result);
	}
}
