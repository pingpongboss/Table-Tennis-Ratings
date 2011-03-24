package wei.mark.tabletennis.util;

import java.util.ArrayList;

import wei.mark.tabletennis.model.PlayerModel;

public interface ProviderParser {
	public static final int MAX_CACHE = 20;
	
	public ArrayList<PlayerModel> playerNameSearch(String query);
	public void onLowMemory();
}
