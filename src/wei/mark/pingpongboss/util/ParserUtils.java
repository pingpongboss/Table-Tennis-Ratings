package wei.mark.pingpongboss.util;

import java.net.URLEncoder;

public class ParserUtils {
	public static String getFirstName(String fullName) {
		int commaIndex = fullName.indexOf(",");
		if (commaIndex != -1)
			return fullName.substring(commaIndex + 1).trim();
		else
			return null;
	}

	public static String getLastName(String fullName) {
		int commaIndex = fullName.indexOf(",");
		if (commaIndex != -1)
			return fullName.substring(0, commaIndex).trim();
		else
			return fullName.trim();
	}

	public static String getSearchUrl(String provider, String query) {
		try {
			if ("usatt".equals(provider)) {
				return String
						.format("http://www.usatt.org/history/rating/history/Allplayers.asp?NSearch=%s",
								URLEncoder.encode(getLastName(query), "UTF-8"));
			} else if ("rc".equals(provider)) {
				return String
						.format("http://www.ratingscentral.com/PlayerList.php?SortOrder=Name&PlayerName=%s",
								URLEncoder.encode(query, "UTF-8"));
			}
		} catch (Exception ex) {
		}
		return null;
	}

	public static String getDetailsUrl(String provider, String id) {
		try {
			if ("usatt".equals(provider)) {
				return String
						.format("http://www.usatt.org/history/rating/history/Phistory.asp?Pid=%s",
								URLEncoder.encode(id, "UTF-8"));
			} else if ("rc".equals(provider)) {
				return String
						.format("http://www.ratingscentral.com/PlayerHistory.php?PlayerID=%s",
								URLEncoder.encode(id, "UTF-8"));
			}
		} catch (Exception ex) {
		}
		return null;
	}

	public static String getEventDetailsUrl(String provider, String playerId,
			String eventId) {
		try {
			if ("usatt".equals(provider)) {
				return String
						.format("http://www.usatt.org/history/rating/history/TResult.asp?Pid=%s&Tid=%s",
								URLEncoder.encode(playerId, "UTF-8"),
								URLEncoder.encode(eventId, "UTF-8"));
			} else if ("rc".equals(provider)) {
				return String
						.format("http://ratingscentral.com/EventDetail.php?EventID=%s#P%s",
								URLEncoder.encode(eventId, "UTF-8"),
								URLEncoder.encode(playerId, "UTF-8"));
			}
		} catch (Exception ex) {
		}
		return null;
	}
}