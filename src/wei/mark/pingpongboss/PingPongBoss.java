package wei.mark.pingpongboss;

import wei.mark.pingpongboss.misc.model.PlayerModel;
import wei.mark.pingpongboss.misc.task.DetailsTask;
import wei.mark.pingpongboss.misc.task.FriendsTask;
import wei.mark.pingpongboss.misc.task.SearchTask;
import wei.mark.pingpongboss.util.FileUtils;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;

public class PingPongBoss extends Application {
	public Facebook facebook;
	public String facebookId;

	public Navigation CurrentNavigation;
	public int CurrentMainViewPagerPosition;

	public PlayerModel CurrentPlayerModel;
	public boolean DualPane;

	public SearchTask usattSearchTask, rcSearchTask;
	public DetailsTask detailsTask;
	public FriendsTask friendsTask;

	public enum Navigation {
		IDLE, LIST, DETAILS
	}

	@Override
	public void onCreate() {
		super.onCreate();

		facebook = new Facebook(getResources().getString(R.string.fb_app_id));

		getSharedPreferences("usatt", 0).edit().clear().commit();
		getSharedPreferences("rc", 0).edit().clear().commit();
		getSharedPreferences("search", 0).edit().clear().commit();

		CurrentNavigation = Navigation.IDLE;
		CurrentMainViewPagerPosition = 0;

		CurrentPlayerModel = null;
	}

	public String getDeviceId() {
		return FileUtils.id(this);
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
