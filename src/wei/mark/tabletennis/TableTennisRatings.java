package wei.mark.tabletennis;

import wei.mark.tabletennis.util.SearchTask;
import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

public class TableTennisRatings extends Application {
	public Navigation CurrentNavigation;
	public ListNavigation CurrentListNavigation;
	public boolean DualPane;
	public SearchTask usattSearchTask, rcSearchTask;
	public String CurrentDebugMessage;

	public enum Navigation {
		IDLE, LIST
	}

	public enum ListNavigation {
		USATT, RC
	}

	@Override
	public void onCreate() {
		super.onCreate();

		getSharedPreferences("usatt", 0).edit().clear().commit();
		getSharedPreferences("rc", 0).edit().clear().commit();
		getSharedPreferences("search", 0).edit().clear().commit();

		CurrentNavigation = Navigation.IDLE;
		CurrentListNavigation = ListNavigation.USATT;
		CurrentDebugMessage = "";
	}

	public String getDeviceId() {
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String id = manager.getDeviceId();

		if (id == null || id.equals("")) {
			id = "unknown";
		}
		return id;
	}
}
