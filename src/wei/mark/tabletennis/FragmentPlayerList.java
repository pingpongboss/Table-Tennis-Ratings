package wei.mark.tabletennis;

import java.util.ArrayList;

import wei.mark.tabletennis.TableTennisRatings.Navigation;
import wei.mark.tabletennis.model.PlayerModel;
import wei.mark.tabletennis.util.Debuggable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPlayerList extends ListFragment {
	TableTennisRatings app;
	ArrayList<PlayerModel> mPlayers;

	public static FragmentPlayerList getInstance(String provider, String query,
			ArrayList<PlayerModel> players) {
		FragmentPlayerList fragment = new FragmentPlayerList();
		Bundle b = new Bundle();
		b.putString("provider", provider);
		b.putString("query", query);
		b.putParcelableArrayList("players", players);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPlayers = new ArrayList<PlayerModel>();
		Bundle b = getArguments();

		ArrayList<Parcelable> items = b.getParcelableArrayList("players");
		if (items != null) {
			for (Parcelable item : items) {
				mPlayers.add((PlayerModel) item);
			}
		}

		setListAdapter(new ArrayAdapter<PlayerModel>(getActivity(),
				android.R.layout.simple_list_item_1, mPlayers));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_player_list, container,
				false);

		TextView titleTextView = (TextView) v.findViewById(R.id.title);
		String provider = getArguments().getString("provider");
		String providerName = null;
		if ("rc".equals(provider))
			providerName = "Ratings Central";
		else if ("usatt".equals(provider))
			providerName = "USATT";
		String query = getArguments().getString("query");
		if (providerName != null && query != null)
			titleTextView.setText(String.format("%s search: %s", providerName,
					query));

		ListFragmentTouchListener l = new ListFragmentTouchListener();
		v.setOnTouchListener(l);
		v.findViewById(android.R.id.list).setOnTouchListener(l);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (TableTennisRatings) getActivity().getApplication();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO
		Toast.makeText(getActivity(),
				String.format("Clicked %s", mPlayers.get(position)),
				Toast.LENGTH_SHORT).show();
	}

	private void debug(String msg) {
		((Debuggable) getActivity()).debug(msg);
	}

	private class ListFragmentTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			app.CurrentNavigation = Navigation.LIST;
			debug("Current navigation is now "
					+ app.CurrentNavigation.toString());
			return false;
		}

	}
}
