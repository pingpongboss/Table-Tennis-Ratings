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
import android.widget.TextView;

public class FragmentPlayerDetails extends ListFragment implements
		DetailsCallback {
	PingPongBoss app;
	ArrayList<EventModel> mEvents;

	PlayerModel mPlayer;

	public static FragmentPlayerDetails getInstance(PlayerModel player) {
		FragmentPlayerDetails fragment = new FragmentPlayerDetails();
		Bundle b = new Bundle();
		b.putParcelable("player", player);
		fragment.setArguments(b);

		return fragment;
	}

	public FragmentPlayerDetails() {
		mEvents = new ArrayList<EventModel>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getActivity().getApplication();

		mPlayer = getArguments().getParcelable("player");

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

		fetchDetails();
	}

	@Override
	public void onPause() {
		super.onPause();

		if (app.detailsTask != null) {
			app.detailsTask.setDetailsCallback(null);
		}
	}

	private void updateCurrentNavigation() {
		app.CurrentNavigation = Navigation.DETAILS;
	}

	private void fetchDetails() {
		try {
			TextView text = (TextView) getView().findViewById(R.id.empty_text);
			text.setVisibility(View.VISIBLE);
			text.setText(R.string.fetching_details);
			getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.retry).setVisibility(View.GONE);
		} catch (Exception ex) {
		}

		boolean anotherRunning = app.detailsTask != null
				&& !app.detailsTask.getPlayer().getProvider()
						.equals(mPlayer.getProvider())
				&& !app.detailsTask.getPlayer().getId().equals(mPlayer.getId());

		if (anotherRunning) {
			app.rcSearchTask.cancel(true);
		}

		if (app.detailsTask == null || anotherRunning) {
			app.detailsTask = new DetailsTask(this);
			app.detailsTask.execute(app.getDeviceId(), mPlayer);
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
}
