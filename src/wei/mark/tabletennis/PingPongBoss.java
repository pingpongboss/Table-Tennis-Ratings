package wei.mark.tabletennis;

import wei.mark.tabletennis.model.PlayerModel;
import wei.mark.tabletennis.util.Installation;
import wei.mark.tabletennis.util.SearchTask;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PingPongBoss extends Application {
	public Navigation CurrentNavigation;
	public PlayerModel CurrentPlayerModel;
	public boolean DualPane;
	public SearchTask usattSearchTask, rcSearchTask;

	public enum Navigation {
		IDLE, LIST, DETAILS
	}

	@Override
	public void onCreate() {
		super.onCreate();

		getSharedPreferences("usatt", 0).edit().clear().commit();
		getSharedPreferences("rc", 0).edit().clear().commit();
		getSharedPreferences("search", 0).edit().clear().commit();

		CurrentNavigation = Navigation.IDLE;
		CurrentPlayerModel = null;
	}

	public String getDeviceId() {
		return Installation.id(this);
	}

	public static Toast getToast(Context context, int imageResourceId,
			String message) {
		View view = getToastView(context, imageResourceId, message);

		Toast toast = new Toast(context);
		toast.setView(view);
		return toast;
	}

	public static View getToastView(Context context, int imageResourceId,
			String message) {
		LayoutInflater inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflator.inflate(R.layout.toast, null);

		ImageView image = (ImageView) view.findViewById(R.id.image);
		TextView text = (TextView) view.findViewById(R.id.text);

		if (imageResourceId != 0) {
			image.setImageResource(imageResourceId);
			image.setPadding(0, 0, 10, 0);
		} else {
			image.setPadding(0, 0, 0, 0);
		}

		if (message != null && !message.equals(""))
			text.setText(message);

		return view;
	}
}
