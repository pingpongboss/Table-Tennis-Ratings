package wei.mark.tabletennis;

import android.app.Application;

public class TableTennisRatings extends Application {
	public Navigation CurrentNavigation;
	public String CurrentDebugMessage = "";
	public boolean DualPane;

	public enum Navigation {
		IDLE, SEARCHING, LIST
	}

}
