package wei.mark.tabletennis.util;

import java.util.ArrayList;

import wei.mark.tabletennis.model.PlayerModel;

public interface ProviderParser {
	public ArrayList<PlayerModel> playerNameSearch(String query);
}
