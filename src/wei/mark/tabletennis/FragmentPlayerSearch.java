package wei.mark.tabletennis;

import java.util.ArrayList;

import org.jared.commons.ui.OnLoadListener;
import org.jared.commons.ui.OnScrollToScreenListener;
import org.jared.commons.ui.WorkspaceView;

import wei.mark.tabletennis.TableTennisRatings.Navigation;
import wei.mark.tabletennis.model.PlayerModel;
import wei.mark.tabletennis.util.ProviderParser;
import wei.mark.tabletennis.util.RatingsCentralParser;
import wei.mark.tabletennis.util.USATTParser;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentPlayerSearch extends Fragment {
	boolean mDualPane;

	String mProvider;
	int mCurrentScreen;
	boolean mSearching;

	ArrayList<String> mRCHistory, mUSATTHistory;
	String mRCQuery, mUSATTQuery;

	ListView rcListView, usattListView;
	Button rcSearchButton, usattSearchButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRCHistory = retrieveHistory("rc");
		mRCQuery = null;
		mUSATTHistory = retrieveHistory("usatt");
		mUSATTQuery = null;
		mCurrentScreen = 0;
		mSearching = false;

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final WorkspaceView work = new WorkspaceView(getActivity(), null);

		View rcView = inflater.inflate(R.layout.fragment_player_search_rc,
				null, false);
		((TextView) rcView.findViewById(R.id.title)).setText("Ratings Central");
		final EditText rcNameInput = (EditText) rcView.findViewById(R.id.rcPlayerNameEditText);

		rcListView = (ListView) rcView.findViewById(android.R.id.list);
		rcListView.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, mRCHistory));
		rcListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				beginSearch("rc", mRCHistory.get(position));
				((TableTennisRatings) getActivity().getApplication()).CurrentNavigation = Navigation.LIST;
			}
		});
		rcListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> l, View v,
					int position, long id) {
				removeFromHistory("rc", mRCHistory.get(position));
				return true;
			}
		});

		rcNameInput.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					beginSearch("rc", ((EditText) v).getText().toString());
					((TableTennisRatings) getActivity().getApplication()).CurrentNavigation = Navigation.LIST;
					return true;
				}
				return false;
			}
		});

		rcSearchButton = (Button) rcView.findViewById(R.id.searchButton);
		rcSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				beginSearch("rc", rcNameInput.getText().toString());
				((TableTennisRatings) getActivity().getApplication()).CurrentNavigation = Navigation.LIST;
			}
		});

		View usattView = inflater.inflate(
				R.layout.fragment_player_search_usatt, null, false);
		((TextView) usattView.findViewById(R.id.title)).setText("USATT");
		final EditText usattNameInput = (EditText) usattView
				.findViewById(R.id.usattPlayerNameEditText);

		usattListView = (ListView) usattView.findViewById(android.R.id.list);
		usattListView.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, mUSATTHistory));
		usattListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				beginSearch("usatt", mUSATTHistory.get(position));
				((TableTennisRatings) getActivity().getApplication()).CurrentNavigation = Navigation.LIST;
			}
		});
		usattListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> l, View v,
					int position, long id) {
				removeFromHistory("usatt", mUSATTHistory.get(position));
				return true;
			}
		});

		usattNameInput.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					beginSearch("usatt", ((EditText) v).getText().toString());
					((TableTennisRatings) getActivity().getApplication()).CurrentNavigation = Navigation.LIST;
					return true;
				}
				return false;
			}
		});

		usattSearchButton = (Button) usattView.findViewById(R.id.searchButton);
		usattSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				beginSearch("usatt", usattNameInput.getText().toString());
				((TableTennisRatings) getActivity().getApplication()).CurrentNavigation = Navigation.LIST;
			}
		});

		work.addView(usattView);
		work.addView(rcView);

		work.setTouchSlop(32);
		work.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {
				work.scrollToScreenImmediate(mCurrentScreen);
			}
		});
		work.setOnScrollToScreenListener(new OnScrollToScreenListener() {

			@Override
			public void OnScrollToScreen(int screen) {
				mCurrentScreen = screen;
				if (screen == 0) {
					usattNameInput.requestFocus();
				} else if (screen == 1) {
					rcNameInput.requestFocus();
				}
			}
		});

		SearchFragmentTouchListener l = new SearchFragmentTouchListener();
		work.setOnTouchListener(l);
		rcView.setOnTouchListener(l);
		usattView.setOnTouchListener(l);
		rcListView.setOnTouchListener(l);
		usattListView.setOnTouchListener(l);
		rcNameInput.setOnTouchListener(l);
		usattNameInput.setOnTouchListener(l);
		rcSearchButton.setOnTouchListener(l);
		usattSearchButton.setOnTouchListener(l);

		return work;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		View contentFrame = getActivity().findViewById(R.id.content);
		mDualPane = contentFrame != null
				&& contentFrame.getVisibility() == View.VISIBLE;

		if (mDualPane) {
			rcListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			usattListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			rcSearchButton.setVisibility(View.GONE);
			usattSearchButton.setVisibility(View.GONE);
		}

		if (mSearching) {
			if ("rc".equals(mProvider)) {
				showProgressDialog(String.format("Searching %s for %s",
						mProvider, mRCQuery), "");
			} else if ("usatt".equals(mProvider)) {
				showProgressDialog(String.format("Searching %s for %s",
						mProvider, mUSATTQuery), "");
			} else {
				showProgressDialog(String.format("Searching %s", mProvider), "");
			}
		} else if (mDualPane
				|| ((TableTennisRatings) getActivity().getApplication()).CurrentNavigation == Navigation.LIST) {
			if ("rc".equals(mProvider)) {
				beginSearch(mProvider, mRCQuery);
			} else if ("usatt".equals(mProvider)) {
				beginSearch(mProvider, mUSATTQuery);
			} else {
				beginSearch(mProvider, null);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		Editor editor = prefs.edit();

		editor.clear();

		int i = 0;
		for (String q : mRCHistory) {
			editor.putString("rchistory" + i++, q);
		}

		i = 0;
		for (String q : mUSATTHistory) {
			editor.putString("usatthistory" + i++, q);
		}
		editor.commit();
	}

	private ArrayList<String> retrieveHistory(String provider) {
		ArrayList<String> history = new ArrayList<String>();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		for (String hist : prefs.getAll().keySet()) {
			if (hist.contains(provider + "history")) {
				String q = prefs.getString(hist, "");
				if (q != "")
					history.add(q);
			}
		}

		if (history.isEmpty()) {
			if ("rc".equals(provider)) {
				history.add("Wei, Mark");
				history.add("Ma, Lin");
			} else if ("usatt".equals(provider)) {
				history.add("Wei");
				history.add("Ma");
			}
		}

		return history;
	}

	private void removeFromHistory(String provider, String item) {
		// TODO confirm
		if ("rc".equals(provider)) {
			mRCHistory.remove(item);
			((ArrayAdapter<?>) rcListView.getAdapter()).notifyDataSetChanged();
		} else if ("usatt".equals(provider)) {
			mUSATTHistory.remove(item);
			((ArrayAdapter<?>) usattListView.getAdapter())
					.notifyDataSetChanged();
		}
	}

	protected void beginSearch(String provider, String query) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

		mProvider = provider;
		if ("rc".equals(provider)) {
			mRCQuery = query;
		} else if ("usatt".equals(provider)) {
			mUSATTQuery = query;
		} else {
			if (mDualPane || provider != null)
				finishSearch(provider, query, null);
			return;
		}

		String providerName;
		if ("rc".equals(provider))
			providerName = "Ratings Central";
		else if ("usatt".equals(provider))
			providerName = "USATT";
		else
			providerName = provider;
		showProgressDialog(
				String.format("Searching %s for %s", providerName, query), "");

		mSearching = true;

		new ProviderSearchTask().execute(provider, query);
	}

	protected void finishSearch(String provider, String query,
			ArrayList<PlayerModel> results) {
		mSearching = false;

		if ("rc".equals(provider)) {
			if (mRCQuery != null) {
				mRCHistory.remove(mRCQuery);
				mRCHistory.add(0, mRCQuery);
				((ArrayAdapter<?>) rcListView.getAdapter())
						.notifyDataSetChanged();

				int position = mRCHistory.indexOf(mRCQuery);
				rcListView.setItemChecked(position, true);

			}
		} else if ("usatt".equals(provider)) {
			if (mUSATTQuery != null) {
				mUSATTHistory.remove(mUSATTQuery);
				mUSATTHistory.add(0, mUSATTQuery);
				((ArrayAdapter<?>) usattListView.getAdapter())
						.notifyDataSetChanged();

				int position = mUSATTHistory.indexOf(mUSATTQuery);
				usattListView.setItemChecked(position, true);

			}
		}

		// remove progress bar dialog
		try {
			getFragmentManager().beginTransaction()
					.remove(getFragmentManager().findFragmentByTag("dialog"))
					.commit();
		} catch (Exception ex) {
		}

		if (mDualPane) {
			FragmentPlayerList fragment = FragmentPlayerList.getInstance(
					provider, query, results);

			getFragmentManager().beginTransaction()
					.replace(R.id.content, fragment)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity(), ActivityPlayerList.class);
			intent.putExtra("provider", provider);
			intent.putExtra("query", query);
			intent.putExtra("players", results);
			startActivity(intent);
		}

		if (results == null)
			((TableTennisRatings) getActivity().getApplication()).CurrentNavigation = Navigation.SEARCH;
	}

	private ProviderParser getProviderParser(String provider) {
		if ("rc".equals(provider)) {
			return RatingsCentralParser.getParser();
		} else if ("usatt".equals(provider)) {
			return USATTParser.getParser();
		} else
			return null;
	}

	private void showProgressDialog(String title, String message) {
		// remove previous progress bar dialog
		try {
			getFragmentManager().beginTransaction()
					.remove(getFragmentManager().findFragmentByTag("dialog"))
					.commit();
		} catch (Exception ex) {
		}

		FragmentProgressBar fragment = FragmentProgressBar.getInstance(title,
				message);
		if (mDualPane) {
			getFragmentManager().beginTransaction()
					.replace(R.id.content, fragment)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
		} else {
			fragment.show(getFragmentManager(), "dialog");
		}
	}

	private class ProviderSearchTask extends
			AsyncTask<String, Void, ArrayList<PlayerModel>> {
		String provider, query;

		@Override
		protected ArrayList<PlayerModel> doInBackground(String... params) {
			try {
				provider = params[0];
				query = params[1];

				ProviderParser parser = getProviderParser(provider);

				return parser.playerNameSearch(query);
			} catch (Exception ex) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<PlayerModel> result) {
			finishSearch(provider, query, result);
		}
	}

	private class SearchFragmentTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			((TableTennisRatings) getActivity().getApplication()).CurrentNavigation = Navigation.SEARCH;
			return false;
		}

	}
}
