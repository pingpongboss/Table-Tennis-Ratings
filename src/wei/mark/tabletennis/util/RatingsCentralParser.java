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

import wei.mark.tabletennis.model.PlayerModel;

public class RatingsCentralParser implements ProviderParser {
	private static RatingsCentralParser mParser;
	private static final String TAG = "RatingsCentralParser";

	private Debuggable mDebuggable;

	private Map<String, ArrayList<PlayerModel>> mCache;

	private RatingsCentralParser(Debuggable debuggable) {
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

	public static synchronized RatingsCentralParser getParser(
			Debuggable debuggable) {
		if (mParser == null)
			mParser = new RatingsCentralParser(debuggable);
		return mParser;
	}

	@Override
	public ArrayList<PlayerModel> playerNameSearch(String query) {
		return lastFirstNamePlayerSearch(query, false);
	}

	public ArrayList<PlayerModel> lastFirstNamePlayerSearch(
			String lastAndFirstName, boolean fresh) {
		ArrayList<PlayerModel> players;
		if (!fresh) {
			// first check cache
			players = mCache.get(lastAndFirstName);
			if (players != null)
				return players;
		}

		try {
			String uri = "http://www.ratingscentral.com/PlayerList.php?PlayerName="
					+ URLEncoder.encode(lastAndFirstName, "UTF-8");
			URL url = new URL(uri);
			XMLReader reader = new Parser();
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			StringWriter sw = new StringWriter();
			transformer.transform(
					new SAXSource(reader, new InputSource(url.openStream())),
					new StreamResult(sw));
			String cleanResponse = sw.toString();

			XPath xpath = XPathFactory.newInstance().newXPath();
			Node result = (Node) xpath.evaluate(
					"//table[@class='Centered Bordered']/tbody",
					new InputSource(new StringReader(cleanResponse)),
					XPathConstants.NODE);

			NodeLogger.LogNode(result, TAG);

			players = new ArrayList<PlayerModel>();
			NodeList playerNodes = result.getChildNodes();

			for (int i = 0; i < playerNodes.getLength(); i++) {
				Node node = playerNodes.item(i);
				PlayerModel player = new PlayerModel();
				player.mProvider = "rc";

				player.mRank = node.getChildNodes().item(0).getTextContent();
				player.mRating = node.getChildNodes().item(1).getTextContent();
				player.mName = node.getChildNodes().item(2).getTextContent();
				player.mId = node.getChildNodes().item(3).getTextContent();
				NodeList clubNodes = node.getChildNodes().item(4)
						.getChildNodes();
				ArrayList<String> clubs = new ArrayList<String>();
				for (int j = 0; j < clubNodes.getLength(); j++) {
					String club = clubNodes.item(j).getTextContent();
					if (club != null && club != "")
						clubs.add(club);
				}
				player.mClubs = clubs.toArray(new String[0]);
				player.mState = node.getChildNodes().item(5).getTextContent();
				player.mCountry = node.getChildNodes().item(6).getTextContent();
				player.mLastPlayed = node.getChildNodes().item(7)
						.getTextContent();
				players.add(player);
			}

			mCache.put(lastAndFirstName, players);
			return players;
		} catch (Exception ex) {
			return mCache.get(lastAndFirstName);
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
