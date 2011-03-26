package wei.mark.tabletennis.util;

import org.w3c.dom.Node;

import android.util.Log;

public class NodeLogger {

	public static void LogNode(Node node, String tag) {
		logNode(node, 0, tag);
	}

	private static void logNode(Node node, int level, String tag) {
		Log.d(tag, getPrefix(level) + node.getTextContent().trim());
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node n = node.getChildNodes().item(i);
			logNode(n, level + 1, tag);
		}
	}

	private static String getPrefix(int level) {
		StringBuilder sb = new StringBuilder();
		if (level == 0)
			return sb.toString();
		
		for (int i = level; i > 0; i--) {
			sb.append("__");
		}
		return sb.toString();
	}
}
