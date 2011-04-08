package wei.mark.tabletennis.util;

import java.util.List;

import wei.mark.tabletennis.R;
import wei.mark.tabletennis.model.PlayerModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlayerModelAdapter extends ArrayAdapter<PlayerModel> {
	ViewHolder holder;
	List<PlayerModel> players;

	public PlayerModelAdapter(Context context, int textViewResourceId,
			List<PlayerModel> objects) {
		super(context, textViewResourceId, objects);
		players = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get references to all the Views
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.item_player_list, parent,
					false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.rating = (TextView) convertView.findViewById(R.id.rating);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// fill in the Views
		PlayerModel player = players.get(position);
		if (player != null) {
			holder.name.setText(player.getName());
			holder.rating.setText(player.getRating());
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	static class ViewHolder {
		TextView name;
		TextView rating;
	}
}
