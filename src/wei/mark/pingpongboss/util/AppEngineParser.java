package wei.mark.pingpongboss.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import wei.mark.pingpongboss.model.EventModel;
import wei.mark.pingpongboss.model.FriendModel;
import wei.mark.pingpongboss.model.PlayerModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AppEngineParser {
	private static AppEngineParser mParser;

	private Map<String, ArrayList<PlayerModel>> mPlayersCache;
	private Map<String, ArrayList<EventModel>> mEventsCache;
	public static final int MAX_CACHE = 100;

	private AppEngineParser() {
		mPlayersCache = new LinkedHashMap<String, ArrayList<PlayerModel>>(
				MAX_CACHE, .75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(
					Map.Entry<String, ArrayList<PlayerModel>> eldest) {
				return size() > MAX_CACHE;
			}
		};
		mEventsCache = new LinkedHashMap<String, ArrayList<EventModel>>(
				MAX_CACHE, .75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(
					Map.Entry<String, ArrayList<EventModel>> eldest) {
				return size() > MAX_CACHE;
			}
		};
	}

	public static synchronized AppEngineParser getParser() {
		if (mParser == null)
			mParser = new AppEngineParser();
		return mParser;
	}

	public String ping(boolean sync) {
		if (sync) {
			return ping();
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					ping();
				}
			}).start();
			return null;
		}
	}

	// synchronous ping
	private String ping() {
		HttpURLConnection connection = null;
		try {
			String uri = "http://ttratings.appspot.com/statuscheck_server";

			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();

			return sb.toString();
		} catch (Exception ex) {
			return null;
		}
	}

	public ArrayList<PlayerModel> search(String id, String provider,
			String query) {
		return search(id, provider, query, false);
	}

	public ArrayList<PlayerModel> search(String id, String provider,
			String query, boolean fresh) {
		ArrayList<PlayerModel> players;

		if (!fresh) {
			// first check cache
			players = mPlayersCache.get(getCacheKey(provider, query));
			if (players != null)
				return players;
		}

		HttpURLConnection connection = null;
		try {
			String uri = String
					.format("http://ttratings.appspot.com/table_tennis_ratings_server?action=search&id=%s&provider=%s&query=%s&fresh=%s",
							URLEncoder.encode(id, "UTF-8"),
							URLEncoder.encode(provider, "UTF-8"),
							URLEncoder.encode(query, "UTF-8"),
							URLEncoder.encode(String.valueOf(fresh), "UTF-8"));

			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();

			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<PlayerModel>>() {
			}.getType();

			players = gson.fromJson(sb.toString(), type);

			mPlayersCache.put(getCacheKey(provider, query), players);
			return players;
		} catch (Exception ex) {
			return mPlayersCache.get(getCacheKey(provider, query));
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	// asynchronously alert server for now. Later, fetch player details
	public ArrayList<EventModel> details(String id, PlayerModel player,
			boolean fresh) {
		ArrayList<EventModel> events;

		if (!fresh) {
			// first check cache
			events = mEventsCache.get(getCacheKey(player.getProvider(),
					player.getId()));
			if (events != null)
				return events;
		}

		HttpURLConnection connection = null;
		try {
			String uri = String
					.format("http://ttratings.appspot.com/table_tennis_ratings_server?action=details&id=%s&provider=%s&query=%s&fresh=%s",
							URLEncoder.encode(id, "UTF-8"),
							URLEncoder.encode(player.getProvider(), "UTF-8"),
							URLEncoder.encode(player.getId(), "UTF-8"),
							URLEncoder.encode(String.valueOf(fresh), "UTF-8"));

			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();

			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<EventModel>>() {
			}.getType();

			events = gson.fromJson(sb.toString(), type);

			mEventsCache.put(getCacheKey(player.getProvider(), player.getId()),
					events);
			return events;
		} catch (Exception ex) {
			return mEventsCache.get(getCacheKey(player.getProvider(),
					player.getId()));
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	public ArrayList<FriendModel> friends(String facebookId, String accessToken) {
		ArrayList<FriendModel> friends;

		// if (!fresh) {
		// // first check cache
		// players = mPlayersCache.get(getCacheKey(provider, query));
		// if (players != null)
		// return players;
		// }

		HttpURLConnection connection = null;
		try {
			String uri = String
					.format("http://ttratings.appspot.com/table_tennis_ratings_server?action=friends&id=%s&query=%s",
							URLEncoder.encode(facebookId, "UTF-8"),
							URLEncoder.encode(accessToken, "UTF-8"));
			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();

			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<FriendModel>>() {
			}.getType();

			friends = gson.fromJson(sb.toString(), type);
			// mPlayersCache.put(getCacheKey(provider, query), players);
			return friends;
		} catch (Exception ex) {
			// return mPlayersCache.get(getCacheKey(provider, query));
			return null;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	private String getCacheKey(String provider, String query) {
		try {
			return provider + query;
		} catch (Exception ex) {
			return null;
		}
	}

	public void onLowMemory() {
		mPlayersCache.clear();
		mPlayersCache = null;
		mParser = null;
	}
}
