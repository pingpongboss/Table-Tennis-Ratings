package wei.mark.pingpongboss.util;

import java.util.ArrayList;

import wei.mark.pingpongboss.model.EventModel;
import wei.mark.pingpongboss.model.PlayerModel;
import android.os.AsyncTask;

public class DetailsTask extends AsyncTask<Object, Void, ArrayList<EventModel>> {
	DetailsCallback callback;
	PlayerModel player;
	String id;
	boolean hasSavedResult;
	ArrayList<EventModel> savedResult;

	public DetailsTask(DetailsCallback detailsCallback) {
		setDetailsCallback(detailsCallback);
	}

	@Override
	protected ArrayList<EventModel> doInBackground(Object... params) {
		try {
			id = (String) params[0];
			player = (PlayerModel) params[1];

			AppEngineParser parser = AppEngineParser.getParser();

			return parser.details(id, player, false);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(ArrayList<EventModel> result) {
		hasSavedResult = true;
		savedResult = result;

		if (callback != null)
			callback.detailsCompleted(savedResult);
	}

	public void setDetailsCallback(DetailsCallback detailsCallback) {
		callback = detailsCallback;

		if (callback != null && hasSavedResult)
			callback.detailsCompleted(savedResult);
	}

	public PlayerModel getPlayer() {
		return player;
	}

	public interface DetailsCallback {

		void detailsCompleted(ArrayList<EventModel> savedResult);

	}

}
