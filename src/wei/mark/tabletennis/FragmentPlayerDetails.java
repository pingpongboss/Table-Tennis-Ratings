package wei.mark.tabletennis;

import wei.mark.tabletennis.PingPongBoss.Navigation;
import wei.mark.tabletennis.model.PlayerModel;
import wei.mark.tabletennis.util.AppEngineParser;
import wei.mark.tabletennisratingsserver.util.ProviderParser.ParserUtils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class FragmentPlayerDetails extends ListFragment {
	PingPongBoss app;
	PlayerModel mPlayer;

	public static FragmentPlayerDetails getInstance(PlayerModel player) {
		FragmentPlayerDetails fragment = new FragmentPlayerDetails();
		Bundle b = new Bundle();
		b.putParcelable("player", player);
		fragment.setArguments(b);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getActivity().getApplication();

		mPlayer = getArguments().getParcelable("player");
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

		v.setOnTouchListener(l);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		fetchDetails();
	}

	private void updateCurrentNavigation() {
		app.CurrentNavigation = Navigation.DETAILS;
	}

	private void fetchDetails() {

		AppEngineParser.getParser().details(app.getDeviceId(), mPlayer);
		// TODO Auto-generated method stub
	}
}
