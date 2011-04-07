package wei.mark.tabletennis.util;

import java.util.ArrayList;

import wei.mark.tabletennis.model.PlayerModel;

public interface SearchCallback {
	public void searchCompleted(ArrayList<PlayerModel> players);
}
