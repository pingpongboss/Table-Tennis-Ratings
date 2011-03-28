package wei.mark.tabletennis.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import wei.mark.tabletennis.model.PlayerModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AppEngineParser {
	private static AppEngineParser mParser;

	private Map<String, ArrayList<PlayerModel>> mCache;
	public static final int MAX_CACHE = 100;

	private AppEngineParser() {
		mCache = new LinkedHashMap<String, ArrayList<PlayerModel>>(MAX_CACHE,
				.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(
					java.util.Map.Entry<String, ArrayList<PlayerModel>> eldest) {
				return size() > MAX_CACHE;
			}
		};
	}

	public static synchronized AppEngineParser getParser() {
		if (mParser == null)
			mParser = new AppEngineParser();
		return mParser;
	}

	public ArrayList<PlayerModel> execute(String id, String provider,
			String query) {
		return execute(id, provider, query, false);
	}

	public ArrayList<PlayerModel> execute(String id, String provider,
			String query, boolean fresh) {
		ArrayList<PlayerModel> players;

		if (!fresh) {
			// first check cache
			players = mCache.get(query);
			if (players != null)
				return players;
		}
		HttpURLConnection connection = null;
		try {
			String uri = String
					.format("http://ttratings.appspot.com/table_tennis_ratings_server?id=%s&provider=%s&query=%s",
							URLEncoder.encode(id, "iso-8859-1"),
							URLEncoder.encode(provider, "iso-8859-1"),
							URLEncoder.encode(query, "iso-8859-1"));

			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "iso-8859-1"));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();

			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<PlayerModel>>() {
			}.getType();

			@SuppressWarnings("unchecked")
			LinkedList<PlayerModel> playersLinkedList = (LinkedList<PlayerModel>) gson
					.fromJson(sb.toString(), type);

			players = new ArrayList<PlayerModel>(playersLinkedList);

			mCache.put(query, players);
			return players;
		} catch (Exception ex) {
			return mCache.get(query);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	public void onLowMemory() {
		mCache.clear();
		mCache = null;
		mParser = null;
	}
}
