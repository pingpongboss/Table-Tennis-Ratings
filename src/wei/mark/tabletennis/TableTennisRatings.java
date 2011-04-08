package wei.mark.tabletennis;

import wei.mark.tabletennis.util.SearchTask;
import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

public class TableTennisRatings extends Application {
	public Navigation CurrentNavigation;
	public boolean DualPane;
	public SearchTask usattSearchTask, rcSearchTask;
	public String CurrentDebugMessage;

	public enum Navigation {
		IDLE, LIST
	}

	@Override
	public void onCreate() {
		super.onCreate();

		getSharedPreferences("listScroll", 0).edit().clear().commit();
		
		CurrentNavigation = Navigation.IDLE;
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
