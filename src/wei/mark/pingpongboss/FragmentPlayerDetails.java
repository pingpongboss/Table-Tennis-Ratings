package wei.mark.pingpongboss;

import java.util.ArrayList;

import wei.mark.pingpongboss.PingPongBoss.Navigation;
import wei.mark.pingpongboss.model.EventModel;
import wei.mark.pingpongboss.model.PlayerModel;
import wei.mark.pingpongboss.util.DetailsTask;
import wei.mark.pingpongboss.util.DetailsTask.DetailsCallback;
import wei.mark.pingpongboss.util.EventModelAdapter;
import wei.mark.tabletennisratingsserver.util.ProviderParser.ParserUtils;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
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

public class FragmentPlayerDetails extends ListFragment implements
		DetailsCallback {
	PingPongBoss app;
	ArrayList<EventModel> mEvents;

	PlayerModel mPlayer;

	String mListProvider, mListId;
	int mListIndex, mListTop;
	boolean mUserChangedScroll;

	public static FragmentPlayerDetails getInstance(PlayerModel player) {
		FragmentPlayerDetails fragment = new FragmentPlayerDetails();
		Bundle b = new Bundle();
		b.putParcelable("player", player);
		fragment.setArguments(b);

		return fragment;
	}

	public FragmentPlayerDetails() {
		mEvents = new ArrayList<EventModel>();

		mListIndex = -1;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getActivity().getApplication();

		setHasOptionsMenu(true);

		mPlayer = getArguments().getParcelable("player");

		SharedPreferences prefs = getActivity().getSharedPreferences("details",
				0);
		mListProvider = prefs.getString("listProvider", null);
		mListId = prefs.getString("listId", null);
		mListIndex = prefs.getInt("listIndex", 0);
		mListTop = prefs.getInt("listTop", 0);

		setListAdapter(new EventModelAdapter(getActivity(),
				R.layout.item_player_event, mEvents));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		OnTouchListener l = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				updateCurrentNavigation();
				return false;
			}
		};

		View v = inflater.inflate(R.layout.fragment_player_details, container,
				false);

		v.findViewById(R.id.logo).setVisibility(View.GONE);

		Button providerLogoButton = (Button) v.findViewById(R.id.provider_logo);

		if ("usatt".equals(mPlayer.getProvider())) {
			providerLogoButton.setBackgroundResource(R.drawable.usatt_selector);
		} else if ("rc".equals(mPlayer.getProvider())) {
			providerLogoButton.setBackgroundResource(R.drawable.rc_selector);
		} else {
			providerLogoButton.setVisibility(View.GONE);
		}
		providerLogoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(ParserUtils.getDetailsUrl(
									mPlayer.getProvider(),
									mPlayer.getProviderId()))));
				} catch (Exception ex) {
				}
			}
		});
		providerLogoButton.setOnTouchListener(l);

		TextView nameTextView = (TextView) v.findViewById(R.id.name);
		nameTextView.setText(mPlayer.getName());
		nameTextView.setOnTouchListener(l);

		TextView idTextView = (TextView) v.findViewById(R.id.id);
		idTextView.setText("#" + mPlayer.getId());
		idTextView.setOnTouchListener(l);

		TextView ratingTextView = (TextView) v.findViewById(R.id.rating);
		ratingTextView.setText(mPlayer.getRating());
		ratingTextView.setOnTouchListener(l);

		TextView fromTextView = (TextView) v.findViewById(R.id.from);
		fromTextView.setText(mPlayer.getState() == null
				|| mPlayer.getState().equals("") ? mPlayer.getCountry()
				: mPlayer.getState());
		fromTextView.setOnTouchListener(l);

		Button retryButton = (Button) v.findViewById(R.id.retry);
		retryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fetchDetails(false);
			}
		});
		retryButton.setOnTouchListener(l);

		v.setOnTouchListener(l);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();

		fetchDetails(false);

		if (mPlayer.getProvider().equals(mListProvider)
				&& mPlayer.getId().equals(mListId))
			getListView().setSelectionFromTop(mListIndex, mListTop);
		else
			getActivity().getSharedPreferences("details", 0).edit().clear()
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

		if (app.detailsTask != null) {
			app.detailsTask.setDetailsCallback(null);
		}

		SharedPreferences prefs = getActivity().getSharedPreferences("details",
				0);
		Editor editor = prefs.edit();

		// save ListView scroll position
		if (mUserChangedScroll) {
			editor.putString("listProvider", mPlayer.getProvider());
			editor.putString("listId", mPlayer.getId());
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
			fetchDetails(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void updateCurrentNavigation() {
		app.CurrentNavigation = Navigation.DETAILS;
	}

	public void fetchDetails(boolean fresh) {
		try {
			mEvents.clear();
			((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();

			TextView text = (TextView) getView().findViewById(R.id.empty_text);
			text.setVisibility(View.VISIBLE);
			text.setText(R.string.fetching_details);
			getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.retry).setVisibility(View.GONE);
		} catch (Exception ex) {
		}

		boolean anotherRunning = false;
		if (app.detailsTask != null) {
			PlayerModel player = app.detailsTask.getPlayer();
			anotherRunning = player != null
					&& !player.getProvider().equals(mPlayer.getProvider())
					&& !player.getId().equals(mPlayer.getId());
		}

		if (anotherRunning) {
			app.rcSearchTask.cancel(true);
		}

		if (app.detailsTask == null || anotherRunning) {
			app.detailsTask = new DetailsTask(this);
			app.detailsTask.execute(app.getDeviceId(), mPlayer, fresh);
		} else {
			app.detailsTask.setDetailsCallback(this);
		}
	}

	@Override
	public void detailsCompleted(ArrayList<EventModel> events) {
		app.detailsTask = null;

		getView().findViewById(R.id.progress).setVisibility(View.GONE);
		TextView text = (TextView) getView().findViewById(R.id.empty_text);

		mEvents.clear();
		if (events != null) {
			mEvents.addAll(events);
			text.setText("No events found");
		} else {
			text.setText("An error occurred");
			text.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.retry).setVisibility(View.VISIBLE);
		}

		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		EventModel event = mEvents.get(position);
		showEventDetails(event);
	}

	protected void showEventDetails(EventModel event) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ParserUtils
				.getEventDetailsUrl(event.getProvider(),
						mPlayer.getProviderId(), event.getId()))));
	}
}
