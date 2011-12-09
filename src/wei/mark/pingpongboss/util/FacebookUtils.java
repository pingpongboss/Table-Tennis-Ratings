package wei.mark.pingpongboss.util;

public class FacebookUtils {
	public static final String GRAPH_PATH_BASE = "http://graph.facebook.com/";
	public static final String GRAPH_PATH_ME = "me";
	public static final String GRAPH_PATH_FRIENDS = "me/friends";
	public static final String INTENT_URL_PROFILE = "fb://profile/";

	public static String getFacebookPictureUrl(String id) {
		return GRAPH_PATH_BASE + id + "/picture?type=square";
	}
}
