package wei.mark.pingpongboss.misc.adapter;

import java.util.ArrayList;

import wei.mark.pingpongboss.R;
import wei.mark.pingpongboss.misc.lazylist.ImageLoader;
import wei.mark.pingpongboss.misc.model.FriendModel;
import wei.mark.pingpongboss.util.FacebookUtils;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendModelAdapter extends ArrayAdapter<FriendModel> {
	ArrayList<FriendModel> friends;
	ArrayList<FriendModel> filteredFriends;
	String lastConstraint = "";

	Activity activity;
	LayoutInflater inflater;
	ImageLoader loader;

	ViewHolder holder;

	public FriendModelAdapter(Activity activity, int textViewResourceId,
			ArrayList<FriendModel> objects) {
		super(activity, textViewResourceId, objects);
		friends = objects;
		filteredFriends = friends;

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
		FriendModel friend = null;
		if (filteredFriends == null)
			friend = friends.get(position);
		else
			friend = filteredFriends.get(position);
		if (friend != null) {
			loader.DisplayImage(
					FacebookUtils.getFacebookPictureUrl(friend.getId()),
					activity, holder.image);
			holder.name.setText(friend.getName());
		}
		return convertView;
	}

	@Override
	public int getCount() {
		if (filteredFriends == null)
			return friends.size();
		return filteredFriends.size();
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				//hack to work around a bug with the results coming back empty
				if (constraint.equals("") && results.count == 0
						&& !friends.isEmpty())
					filteredFriends = friends;
				else
					filteredFriends = (ArrayList<FriendModel>) results.values;
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				lastConstraint = constraint.toString();
				constraint = constraint.toString().toLowerCase();

				ArrayList<FriendModel> list = new ArrayList<FriendModel>();
				for (FriendModel friendModel : friends) {
					if (friendModel.getName().toLowerCase()
							.contains(constraint))
						list.add(friendModel);
				}

				FilterResults results = new FilterResults();
				results.values = list;
				results.count = list.size();
				return results;
			}
		};
	}

	public ImageLoader getLoader() {
		return loader;
	}

	public FriendModel getFriendModel(int position) {
		return filteredFriends.get(position);
	}

	static class ViewHolder {
		ImageView image;
		TextView name;
	}
}
