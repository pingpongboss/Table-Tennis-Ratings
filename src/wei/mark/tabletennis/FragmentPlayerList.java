package wei.mark.tabletennis;

import java.util.ArrayList;

import wei.mark.tabletennis.TableTennisRatings.Navigation;
import wei.mark.tabletennis.model.PlayerModel;
import wei.mark.tabletennis.util.Debuggable;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPlayerList extends ListFragment {
	TableTennisRatings app;
	ArrayList<PlayerModel> mPlayers;
	String mQuery;
	int mListIndex, mListTop;
	boolean mUserScroll;

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

	public FragmentPlayerList() {
		mPlayers = new ArrayList<PlayerModel>();
		mQuery = null;
		mListIndex = -1;
		mListTop = 0;
		mUserScroll = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();

		ArrayList<Parcelable> items = b.getParcelableArrayList("players");
		if (items != null) {
			for (Parcelable item : items) {
				mPlayers.add((PlayerModel) item);
			}
		}

		mQuery = b.getString("query");

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

		Button logoButton = (Button) v.findViewById(R.id.logo);
		if ("rc".equals(provider))
			logoButton.setBackgroundResource(R.drawable.rc_selector);
		else if ("usatt".equals(provider))
			logoButton.setBackgroundResource(R.drawable.usatt_selector);

		ListFragmentTouchListener l = new ListFragmentTouchListener();
		v.setOnTouchListener(l);
		v.findViewById(android.R.id.list).setOnTouchListener(l);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		app = (TableTennisRatings) getActivity().getApplication();

		SharedPreferences prefs = getActivity().getSharedPreferences(
				"listScroll", 0);

		if (prefs.getString("listQuery", "").equals(mQuery)) {
			int index = prefs.getInt("listIndex", 0);
			int top = prefs.getInt("listTop", 0);
			getListView().setSelectionFromTop(index, top);
		}

		getListView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mUserScroll = true;
				return false;
			}
		});

		getListView().setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mUserScroll) {
					mListIndex = firstVisibleItem;
					View firstView = view.getChildAt(0);
					mListTop = firstView == null ? 0 : firstView.getTop();
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();

		mUserScroll = false;

		if (mListIndex != -1) {
			Editor edit = getActivity().getSharedPreferences("listScroll", 0)
					.edit();
			edit.putString("listQuery", mQuery);
			edit.putInt("listIndex", mListIndex);
			edit.putInt("listTop", mListTop);

			edit.commit();
		}
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
