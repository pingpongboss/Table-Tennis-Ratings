package wei.mark.pingpongboss.util;

import java.util.List;

import wei.mark.pingpongboss.R;
import wei.mark.pingpongboss.model.FriendModel;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

public class FriendModelAdapter extends ArrayAdapter<FriendModel> {
	List<FriendModel> friends;

	Activity activity;
	LayoutInflater inflater;
	ImageLoader loader;

	ViewHolder holder;

	public FriendModelAdapter(Activity activity, int textViewResourceId,
			List<FriendModel> objects) {
		super(activity, textViewResourceId, objects);
		friends = objects;

		this.activity = activity;
		inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		loader = new ImageLoader(activity);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get references to all the Views
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_player_friends,
					parent, false);

			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// fill in the Views
		FriendModel friend = friends.get(position);
		if (friend != null) {
			loader.DisplayImage(getFacebookPictureUrl(friend.getId()),
					activity, holder.image);
			holder.name.setText(friend.getName());
		}
		return convertView;
	}

	private String getFacebookPictureUrl(String id) {
		return Constants.GRAPH_PATH_BASE + id + "/picture";
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	static class ViewHolder {
		ImageView image;
		TextView name;
	}
}
