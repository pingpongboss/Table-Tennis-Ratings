package wei.mark.pingpongboss.misc.task;

import wei.mark.pingpongboss.util.ServerUtils;
import android.os.AsyncTask;

public class LinkTask extends AsyncTask<String, Void, Void> {
	String id, playerId, facebookId, editor;

	@Override
	protected Void doInBackground(String... params) {
		try {
			id = params[0];
			playerId = params[1];
			facebookId = params[2];
			editor = params[3];

			ServerUtils.link(id, playerId, facebookId, editor);

			return null;
		} catch (Exception ex) {
			return null;
		}
	}
}
