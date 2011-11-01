package wei.mark.pingpongboss;

import java.util.ArrayList;

import wei.mark.pingpongboss.PingPongBoss.Navigation;
import wei.mark.pingpongboss.model.PlayerModel;
import wei.mark.pingpongboss.model.Refreshable;
import wei.mark.pingpongboss.util.ParserUtils;
import wei.mark.pingpongboss.util.PlayerModelAdapter;
import wei.mark.pingpongboss.util.SearchTask;
import wei.mark.pingpongboss.util.SearchTask.SearchCallback;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentPlayerList extends ListFragment implements SearchCallback {
	PingPongBoss app;
	ArrayList<PlayerModel> mPlayers;
	
	String mProvider, mQuery, mListQuery;
	
	int mListIndex, mListTop;
	boolean mUserChangedScroll, mUser;

	public static FragmentPlayerList getInstance(String provider, String query,
			boolean user) {
		FragmentPlayerList fragment = new FragmentPlayerList();
		Bundle b = new Bundle();
		b.putString("provider", provider);
		b.putString("query", query);
		b.putBoolean("user", user);
		fragment.setArguments(b);

		return fragment;
	}

	public FragmentPlayerList() {
		mPlayers = new ArrayList<PlayerModel>();
		mProvider = null;
		mQuery = null;
		mListQuery = null;
		mListIndex = -1;
		mListTop = 0;
		mUserChangedScroll = false;
		mUser = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getActivity().getApplication();
		
		setHasOptionsMenu(true);

		Bundle b = getArguments();

		mProvider = b.getString("provider");
		mQuery = b.getString("query");
		mUser = b.getBoolean("user");

		SharedPreferences prefs = getActivity().getSharedPreferences(mProvider,
				0);
		mListQuery = prefs.getString("listQuery", null);
		mListIndex = prefs.getInt("listIndex", 0);
		mListTop = prefs.getInt("listTop", 0);

		setListAdapter(new PlayerModelAdapter(getActivity(),
				R.layout.item_player_list, mPlayers));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_player_list, container,
				false);

		TextView titleTextView = (TextView) v.findViewById(R.id.title);
		String providerName = null;
		if ("rc".equals(mProvider))
			providerName = "Ratings Central";
		else if ("usatt".equals(mProvider))
			providerName = "USATT";
		if (providerName != null && mQuery != null)
			titleTextView.setText(String.format("%s search: %s", providerName,
					mQuery));

		v.findViewById(R.id.logo).setVisibility(View.GONE);

		Button providerLogoButton = (Button) v.findViewById(R.id.provider_logo);

		if ("usatt".equals(mProvider)) {
			providerLogoButton.setBackgroundResource(R.drawable.usatt_selector);
		} else if ("rc".equals(mProvider)) {
			providerLogoButton.setBackgroundResource(R.drawable.rc_selector);
		} else {
			providerLogoButton.setVisibility(View.GONE);
		}
		providerLogoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(ParserUtils.getSearchUrl(mProvider, mQuery))));
				} catch (Exception ex) {
				}
			}
		});

		Button retryButton = (Button) v.findViewById(R.id.retry);
		retryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startSearch(false);
			}
		});

		OnTouchListener l = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				updateCurrentNavigation();
				return false;
			}
		};
		v.setOnTouchListener(l);
		providerLogoButton.setOnTouchListener(l);
		retryButton.setOnTouchListener(l);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (app.CurrentNavigation == Navigation.DETAILS
				&& mProvider.equals(app.CurrentPlayerModel.getProvider())) {
			showDetails(app.CurrentPlayerModel);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		startSearch(false);

		if (mQuery.equals(mListQuery))
			getListView().setSelectionFromTop(mListIndex, mListTop);
		else
			getActivity().getSharedPreferences(mProvider, 0).edit().clear()
					.commit();

		getListView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mUserChangedScroll = true;

				updateCurrentNavigation();

				return false;
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();

		if ("usatt".equals(mProvider)) {
			if (app.usattSearchTask != null)
				app.usattSearchTask.setSearchCallback(null);
		} else if ("rc".equals(mProvider)) {
			if (app.rcSearchTask != null)
				app.rcSearchTask.setSearchCallback(null);
		}

		SharedPreferences prefs = getActivity().getSharedPreferences(mProvider,
				0);
		Editor editor = prefs.edit();

		// save ListView scroll position
		if (mUserChangedScroll) {
			editor.putString("listQuery", mQuery);
			editor.putInt("listIndex", getListView().getFirstVisiblePosition());
			View rcv = getListView().getChildAt(0);
			editor.putInt("listTop", rcv == null ? 0 : rcv.getTop());
		}

		editor.commit();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (menu.findItem(R.id.refresh) == null)
			inflater.inflate(R.menu.result_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			((Refreshable)getActivity()).refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void updateCurrentNavigation() {
		app.CurrentNavigation = Navigation.LIST;
	}

	public void startSearch(boolean fresh) {
		try {
			mPlayers.clear();
			((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
			
			TextView text = (TextView) getView().findViewById(
					R.id.emptyListText);
			text.setVisibility(View.VISIBLE);
			text.setText(R.string.searching);
			getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.retry).setVisibility(View.GONE);
		} catch (Exception ex) {
		}

		if ("usatt".equals(mProvider)) {
			if (app.usattSearchTask == null) {
				app.usattSearchTask = new SearchTask(this);
				app.usattSearchTask.execute(app.getDeviceId(), mProvider,
						mQuery, String.valueOf(mUser), String.valueOf(fresh));
			} else if (app.usattSearchTask.getQuery() != mQuery) {
				app.usattSearchTask.cancel(true);
				app.usattSearchTask = new SearchTask(this);
				app.usattSearchTask.execute(app.getDeviceId(), mProvider,
						mQuery, String.valueOf(mUser), String.valueOf(fresh));
			}
			app.usattSearchTask.setSearchCallback(this);
		} else if ("rc".equals(mProvider)) {
			if (app.rcSearchTask == null) {
				app.rcSearchTask = new SearchTask(this);
				app.rcSearchTask.execute(app.getDeviceId(), mProvider, mQuery,
						String.valueOf(mUser), String.valueOf(fresh));
			} else if (app.rcSearchTask.getQuery() != mQuery) {
				app.rcSearchTask.cancel(true);
				app.rcSearchTask = new SearchTask(this);
				app.rcSearchTask.execute(app.getDeviceId(), mProvider, mQuery,
						String.valueOf(mUser), String.valueOf(fresh));
			}
			app.rcSearchTask.setSearchCallback(this);
		}
	}

	@Override
	public void searchCompleted(ArrayList<PlayerModel> players) {
		if ("usatt".equals(mProvider)) {
			app.usattSearchTask = null;
		} else if ("rc".equals(mProvider)) {
			app.rcSearchTask = null;
		}

		TextView text = (TextView) getView().findViewById(R.id.emptyListText);
		getView().findViewById(R.id.progress).setVisibility(View.GONE);

		mPlayers.clear();
		if (players != null) {
			mPlayers.addAll(players);
			text.setText("No search results");
		} else {
			text.setText("An error occurred");
			text.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.retry).setVisibility(View.VISIBLE);
		}

		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		PlayerModel player = mPlayers.get(position);

		showDetails(player);
	}

	protected void showDetails(PlayerModel player) {
		if (player == null)
			return;

		if (app.DualPane) {
			FragmentPlayerDetails fragment = FragmentPlayerDetails
					.getInstance(player);
			getFragmentManager().beginTransaction()
					.replace(R.id.content, fragment, "details")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.addToBackStack("details").commit();
		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity(), ActivityPlayerDetails.class);
			intent.putExtra("player", player);
			startActivity(intent);
		}

		app.CurrentNavigation = Navigation.DETAILS;
		app.CurrentPlayerModel = player;
	}
}
