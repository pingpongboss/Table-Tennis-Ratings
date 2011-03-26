package wei.mark.tabletennis.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.SystemClock;

import wei.mark.tabletennis.model.PlayerModel;

public class USATTParser implements ProviderParser {
	private static USATTParser mParser;
	private static final String TAG = "USATTParser";

	private Debuggable mDebuggable;

	private Map<String, ArrayList<PlayerModel>> mCache;

	private USATTParser(Debuggable debuggable) {
		mCache = new LinkedHashMap<String, ArrayList<PlayerModel>>(MAX_CACHE,
				.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(
					java.util.Map.Entry<String, ArrayList<PlayerModel>> eldest) {
				return size() > MAX_CACHE;
			}
		};
		mDebuggable = debuggable;
	}

	public static synchronized USATTParser getParser(Debuggable debuggable) {
		if (mParser == null)
			mParser = new USATTParser(debuggable);
		return mParser;
	}

	@Override
	public ArrayList<PlayerModel> playerNameSearch(String query) {
		return playerNameSearch(query, false);
	}

	public ArrayList<PlayerModel> playerNameSearch(String lastName,
			boolean fresh) {
		ArrayList<PlayerModel> players;
		long time = SystemClock.uptimeMillis();
		long newTime = SystemClock.uptimeMillis();
		long elapsed = 0;

		newTime = SystemClock.uptimeMillis();
		elapsed = newTime - time;
		time = newTime;
		debug("Checking cache... " + elapsed);

		if (!fresh) {
			// first check cache
			players = mCache.get(lastName);
			if (players != null)
				return players;
		}

		try {
			newTime = SystemClock.uptimeMillis();
			elapsed = newTime - time;
			time = newTime;
			debug("Preparing data retrieval... " + elapsed);
			String uri = "http://www.usatt.org/history/rating/history/allplayerslist.asp?ratings_selection=Last+Name&choose_ratings_by="
					+ URLEncoder.encode(lastName, "UTF-8");
			URL url = new URL(uri);
			XMLReader reader = new Parser();
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			StringWriter sw = new StringWriter();

			newTime = SystemClock.uptimeMillis();
			elapsed = newTime - time;
			time = newTime;
			debug("Downloading data... " + elapsed);
			transformer.transform(
					new SAXSource(reader, new InputSource(url.openStream())),
					new StreamResult(sw));
			String cleanResponse = sw.toString();

			XPath xpath = XPathFactory.newInstance().newXPath();

			newTime = SystemClock.uptimeMillis();
			elapsed = newTime - time;
			time = newTime;
			debug("Parsing data... " + elapsed);
			Node result = (Node) xpath.evaluate("/html/body/table",
					new InputSource(new StringReader(cleanResponse)),
					XPathConstants.NODE);

			NodeLogger.LogNode(result, TAG);

			players = new ArrayList<PlayerModel>();
			NodeList playerNodes = result.getChildNodes();

			newTime = SystemClock.uptimeMillis();
			elapsed = newTime - time;
			time = newTime;
			debug("Generating players... " + elapsed);
			// 0th child is headers
			for (int i = 1; i < playerNodes.getLength(); i++) {
				Node node = playerNodes.item(i);
				PlayerModel player = new PlayerModel();
				player.mProvider = "usatt";

				player.mId = node.getChildNodes().item(1).getTextContent()
						.trim();
				player.mExpires = node.getChildNodes().item(2).getTextContent()
						.trim();
				player.mName = node.getChildNodes().item(3).getTextContent()
						.trim();
				player.mRating = node.getChildNodes().item(4).getTextContent()
						.trim();
				player.mState = node.getChildNodes().item(5).getTextContent()
						.trim();
				player.mLastPlayed = node.getChildNodes().item(6)
						.getTextContent().trim();
				players.add(player);
			}

			newTime = SystemClock.uptimeMillis();
			elapsed = newTime - time;
			time = newTime;
			debug("Updating cache... " + elapsed);
			mCache.put(lastName, players);
			return players;
		} catch (Exception ex) {
			return mCache.get(lastName);
		}
	}

	@Override
	public void onLowMemory() {
		mCache.clear();
		mCache = null;
		mParser = null;
	}

	private void debug(String msg) {
		mDebuggable.debug(msg);
	}
}
