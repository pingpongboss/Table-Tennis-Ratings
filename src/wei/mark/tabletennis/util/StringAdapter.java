package wei.mark.tabletennis.util;

import java.util.List;

import wei.mark.tabletennis.R;
import android.app.ListActivity;
import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

public class StringAdapter extends ArrayAdapter<String> {
	ViewHolder holder;
	List<String> strings;
	ListFragment listFragment;
	ListActivity listActivity;

	public StringAdapter(Context context, int textViewResourceId,
			List<String> objects, ListFragment fragment) {
		super(context, textViewResourceId, objects);
		strings = objects;
		listFragment = fragment;
	}

	public StringAdapter(Context context, int textViewResourceId,
			List<String> objects, ListActivity activity) {
		super(context, textViewResourceId, objects);
		strings = objects;
		listActivity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get references to all the Views
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.item_player_search, parent,
					false);

			holder = new ViewHolder();
			holder.name = (CheckedTextView) convertView.findViewById(R.id.name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// fill in the Views
		String s = strings.get(position);
		if (s != null) {
			holder.name.setText(s);
			if (listFragment != null)
				holder.name.setChecked(listFragment.getListView()
						.getCheckedItemPosition() == position);
			else if (listActivity != null)
				holder.name.setChecked(listActivity.getListView()
						.getCheckedItemPosition() == position);
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	static class ViewHolder {
		CheckedTextView name;
	}
}
