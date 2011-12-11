package wei.mark.pingpongboss.misc.task;

import wei.mark.pingpongboss.util.ServerUtils;
import android.os.AsyncTask;

public class LinkTask extends AsyncTask<String, Void, Void> {
	String id, playerId, provider, facebookId, editor;

	@Override
	protected Void doInBackground(String... params) {
		try {
			id = params[0];
			playerId = params[1];
			provider = params[2];
			facebookId = params[3];
			editor = params[4];

			ServerUtils.link(id, playerId, provider, facebookId, editor);

			return null;
		} catch (Exception ex) {
			return null;
		}
	}
}
