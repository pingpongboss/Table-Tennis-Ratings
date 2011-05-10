package wei.mark.pingpongboss.util;

import java.util.ArrayList;

import wei.mark.pingpongboss.model.PlayerModel;

public interface SearchCallback {
	public void searchCompleted(ArrayList<PlayerModel> players);
}
