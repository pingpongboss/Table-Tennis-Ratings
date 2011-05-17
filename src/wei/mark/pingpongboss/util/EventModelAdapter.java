package wei.mark.pingpongboss.util;

import java.util.List;

import wei.mark.pingpongboss.R;
import wei.mark.pingpongboss.model.EventModel;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventModelAdapter extends ArrayAdapter<EventModel> {
	Context mContext;
	List<EventModel> mEvents;

	public EventModelAdapter(Context context, int textViewResourceId,
			List<EventModel> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
		mEvents = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.item_player_event, parent,
					false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.after = (TextView) convertView.findViewById(R.id.after);
			holder.change = (TextView) convertView.findViewById(R.id.change);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// fill in the Views
		EventModel event = mEvents.get(position);
		if (event != null) {
			holder.name.setText(event.getName());
			holder.date.setText(event.getDate());
			holder.after.setText(event.getRatingAfter());

			String changeStringWithSign = event.getRatingChange()
					.replace('−', '-')
					.replaceAll(new Character((char) 160).toString(), "");

			String changeString = changeStringWithSign
					.substring(changeStringWithSign.indexOf('+') + 1);
			int change = Integer.parseInt(changeString);

			if (change != 0 && changeStringWithSign.charAt(0) != '+'
					&& changeStringWithSign.charAt(0) != '-')
				changeStringWithSign = '+' + changeStringWithSign;

			holder.change.setText(changeStringWithSign);

			if (change > 0) {
				holder.change.setTextColor(Color.GREEN);
			} else if (change < 0) {
				holder.change.setTextColor(Color.RED);
			} else {
				holder.change.setTextColor(mContext.getResources().getColor(
						R.color.secondary_text));
			}
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	static class ViewHolder {
		TextView name;
		TextView date;
		TextView after;
		TextView change;
	}
}
