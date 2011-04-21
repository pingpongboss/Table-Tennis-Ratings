package wei.mark.tabletennis;

import java.util.ArrayList;

import wei.mark.tabletennis.TableTennisRatings.ListNavigation;
import wei.mark.tabletennis.TableTennisRatings.Navigation;
import wei.mark.tabletennis.model.PlayerModel;
import wei.mark.tabletennis.util.PlayerModelAdapter;
import wei.mark.tabletennis.util.SearchCallback;
import wei.mark.tabletennis.util.SearchTask;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
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
	TableTennisRatings app;
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
		app = (TableTennisRatings) getActivity().getApplication();

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
		if (!app.DualPane) {
			providerLogoButton.setVisibility(View.GONE);
		} else if ("usatt".equals(mProvider)) {
			providerLogoButton.setBackgroundResource(R.drawable.usatt_selector);
			providerLogoButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://www.usatt.org/")));
				}
			});
		} else if ("rc".equals(mProvider)) {
			providerLogoButton.setBackgroundResource(R.drawable.rc_selector);
			providerLogoButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://www.ratingscentral.com/")));
				}
			});
		} else {
			providerLogoButton.setVisibility(View.GONE);
		}

		Button retryButton = (Button) v.findViewById(R.id.retry);
		retryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startSearch();
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

		startSearch();

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
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO
		TableTennisRatings.getToast(getActivity(), 0,
				String.format("Clicked %s", mPlayers.get(position))).show();
	}

	private void updateCurrentNavigation() {
		app.CurrentNavigation = Navigation.LIST;

		if ("usatt".equals(mProvider))
			app.CurrentListNavigation = ListNavigation.USATT;
		else if ("rc".equals(mProvider))
			app.CurrentListNavigation = ListNavigation.RC;
	}

	private void startSearch() {
		TextView text = null;
		try {
			text = (TextView) getView().findViewById(R.id.emptyListText);
			text.setVisibility(View.GONE);
			text.setText("No search results");
			getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.retry).setVisibility(View.GONE);
		} catch (Exception ex) {
		}

		if ("usatt".equals(mProvider)) {
			if (app.usattSearchTask == null) {
				app.usattSearchTask = new SearchTask(this);
				app.usattSearchTask.execute(TableTennisRatings.getDeviceId(),
						mProvider, mQuery, String.valueOf(mUser));
			} else if (app.usattSearchTask.getQuery() != mQuery) {
				app.usattSearchTask.cancel(true);
				app.usattSearchTask = new SearchTask(this);
				app.usattSearchTask.execute(TableTennisRatings.getDeviceId(),
						mProvider, mQuery, String.valueOf(mUser));
			}
			app.usattSearchTask.setSearchCallback(this);
		} else if ("rc".equals(mProvider)) {
			if (app.rcSearchTask == null) {
				app.rcSearchTask = new SearchTask(this);
				app.rcSearchTask.execute(TableTennisRatings.getDeviceId(),
						mProvider, mQuery, String.valueOf(mUser));
			} else if (app.rcSearchTask.getQuery() != mQuery) {
				app.rcSearchTask.cancel(true);
				app.rcSearchTask = new SearchTask(this);
				app.rcSearchTask.execute(TableTennisRatings.getDeviceId(),
						mProvider, mQuery, String.valueOf(mUser));
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

		TextView text = null;
		try {
			text = (TextView) getView().findViewById(R.id.emptyListText);
			text.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.progress).setVisibility(View.GONE);
		} catch (Exception ex) {
		}

		if (players != null)
			mPlayers.addAll(players);
		else if (text != null) {
			text.setText("An error occurred");
			getView().findViewById(R.id.retry).setVisibility(View.VISIBLE);
		}

		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}
}
