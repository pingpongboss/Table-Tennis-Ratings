package wei.mark.pingpongboss;

import java.util.ArrayList;
import java.util.Arrays;

import wei.mark.pingpongboss.PingPongBoss.Navigation;
import wei.mark.pingpongboss.util.AppEngineParser;
import wei.mark.pingpongboss.util.FileUtils;
import wei.mark.pingpongboss.util.StringAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPlayerSearch extends ListFragment {
	public static final String TAG = "FragmentPlayerSearch";
	PingPongBoss app;

	int mListIndex, mListTop;
	boolean mUserChangedScroll;

	ArrayList<String> mHistory;
	String mQuery, mPreviousInput;

	EditText searchInput;
	ImageButton searchButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PingPongBoss) getActivity().getApplication();

		setHasOptionsMenu(true);

		mHistory = retrieveHistory();

		SharedPreferences prefs = getActivity().getSharedPreferences("search",
				0);
		mQuery = prefs.getString("query", null);
		mPreviousInput = prefs.getString("input", null);
		mListIndex = prefs.getInt("listIndex", 0);
		mListTop = prefs.getInt("listTop", 0);

		setListAdapter(new StringAdapter(getActivity(),
				R.layout.item_player_search, mHistory, this));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_player_search, null,
				false);

		((TextView) view.findViewById(R.id.title)).setText("pingpongboss");

		searchInput = (EditText) view.findViewById(R.id.searchEditText);
		if (mPreviousInput != null) {
			searchInput.setText(mPreviousInput);
		}

		searchInput.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					search(((EditText) v).getText().toString(), true);
				}
				return false;
			}
		});

		searchButton = (ImageButton) view.findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search(searchInput.getText().toString(), true);
			}
		});

		OnTouchListener l = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				updateCurrentNavigation();
				return false;
			}
		};

		view.setOnTouchListener(l);
		searchInput.setOnTouchListener(l);
		searchButton.setOnTouchListener(l);
		view.findViewById(R.id.logo).setOnTouchListener(l);

		return view;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String query = mHistory.get(position);
		searchInput.setText(query);
		search(query, true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> l, View v,
					int position, long id) {
				removeFromHistory(mHistory.get(position));
				return true;
			}
		});

		getListView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mUserChangedScroll = true;
				updateCurrentNavigation();
				return false;
			}
		});

		getListView().setSelectionFromTop(mListIndex, mListTop);

		View contentFrame = getActivity().findViewById(R.id.content);
		app.DualPane = contentFrame != null
				&& contentFrame.getVisibility() == View.VISIBLE;

		if (app.DualPane) {
			// hide promo rotate
			Editor edit = PreferenceManager.getDefaultSharedPreferences(
					getActivity()).edit();
			edit.putBoolean("promo_rotate", true);
			edit.commit();

			// set list properties
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			// tweak search input for narrow column
			searchInput.setBackgroundResource(R.drawable.white_selector);
			searchButton.setVisibility(View.GONE);
			getResources().getConfiguration();
			int size = getResources().getConfiguration().screenLayout
					& Configuration.SCREENLAYOUT_SIZE_MASK;
			getResources().getConfiguration();
			if (size < Configuration.SCREENLAYOUT_SIZE_LARGE
					|| getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				getView().findViewById(R.id.logo).setBackgroundResource(
						R.drawable.logo_small_selector);
			}

			ViewStub input_stub = (ViewStub) getActivity().findViewById(
					R.id.promo_search_stub_input);
			if (input_stub != null) {
				// show promo search
				View input_view = input_stub.inflate();
				input_view
						.setBackgroundResource(R.drawable.toast_frame_left_tip);
				ViewStub history_stub = (ViewStub) getActivity().findViewById(
						R.id.promo_search_stub_history);
				View history_view = history_stub.inflate();
				history_view
						.setBackgroundResource(R.drawable.toast_frame_left_tip);

				int top_input = getResources().getDrawable(
						R.drawable.logo_small_selector).getIntrinsicHeight();
				RelativeLayout.LayoutParams params_input = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				params_input.setMargins(0, top_input, 0, 0);
				input_view.setLayoutParams(params_input);

				RelativeLayout.LayoutParams params_history = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				params_history.addRule(RelativeLayout.BELOW,
						R.id.promo_search_input);
				history_view.setLayoutParams(params_history);

				((TextView) input_view.findViewById(R.id.text))
						.setText(R.string.promo_search_input);
				((TextView) history_view.findViewById(R.id.text))
						.setText(R.string.promo_search_history);
			}
		} else {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity());

			// show promo_rotate below list
			boolean rotated = prefs.getBoolean("promo_rotate", false);
			int newHistoryCount = getNewHistoryCount();
			if (!rotated && newHistoryCount <= 2) {
				ViewStub stub = (ViewStub) getView().findViewById(
						R.id.promo_rotate_stub);
				if (stub != null) {
					View view = stub.inflate();
					ImageView image = (ImageView) view.findViewById(R.id.image);
					image.setImageResource(R.drawable.promo_rotate);
					image.setPadding(0, 0, 10, 0);
					((TextView) view.findViewById(R.id.text))
							.setText(getResources().getString(
									R.string.promo_rotate));
				}
			}
		}

		if (app.CurrentNavigation == Navigation.LIST
				|| app.CurrentNavigation == Navigation.DETAILS || app.DualPane) {
			search(mQuery, false);
		}

		searchInput.requestFocus();
	}

	@Override
	public void onPause() {
		super.onPause();

		SharedPreferences prefs = getActivity().getSharedPreferences("search",
				0);
		Editor editor = prefs.edit();

		// save ListView scroll position
		if (mUserChangedScroll) {
			editor.putInt("listIndex", getListView().getFirstVisiblePosition());
			View rcv = getListView().getChildAt(0);
			editor.putInt("listTop", rcv == null ? 0 : rcv.getTop());
		}

		editor.putString("query", mQuery);
		editor.putString("input", searchInput.getText().toString());

		editor.commit();

		// save search history
		saveHistory();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mQuery = null;
		AppEngineParser.getParser().onLowMemory();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (menu.findItem(R.id.import_item) == null)
			inflater.inflate(R.menu.search_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.import_item: {
			ArrayList<String> history = FileUtils.importHistory();
			String message = null;
			int counter = 0;
			if (history != null) {
				for (String query : history) {
					if (!mHistory.contains(query)) {
						mHistory.add(query);
						counter++;
					}
				}
				((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
				saveHistory();

				message = String.format("Imported %d new queries.", counter);
			} else {
				message = "Import failed.";
			}
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
			return true;
		}
		case R.id.export:
			boolean success = FileUtils.exportHistory(mHistory);
			String message = null;
			if (success) {
				message = String.format("Search history exported to\n%s/%s.",
						Environment.getExternalStorageDirectory()
								.getAbsolutePath(), FileUtils.HISTORY);
			} else {
				message = "Export failed.";
			}
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private ArrayList<String> retrieveHistory() {
		ArrayList<String> history = new ArrayList<String>();
		SharedPreferences prefs = getActivity().getSharedPreferences("history",
				0);

		int i = 0;
		String q;

		while ((q = prefs.getString(String.valueOf(i++), null)) != null) {
			history.add(q);
		}

		if (history.isEmpty()) {
			history.addAll(Arrays.asList(getResources().getStringArray(
					R.array.example_searches)));
		}

		return history;
	}

	private void saveHistory() {
		SharedPreferences prefs = getActivity().getSharedPreferences("history",
				0);
		Editor editor = prefs.edit();

		editor.clear();

		int i = 0;
		for (String q : mHistory) {
			editor.putString(String.valueOf(i++), q);
		}

		editor.commit();
	}

	private void removeFromHistory(final String item) {
		new AlertDialog.Builder(getActivity())
				.setTitle(String.format("Remove %s from History?", item))
				.setPositiveButton("Remove",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mHistory.remove(item);
								((ArrayAdapter<?>) getListAdapter())
										.notifyDataSetChanged();
								saveHistory();
							}
						})
				.setNeutralButton("Clear All",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mHistory.clear();
								((ArrayAdapter<?>) getListAdapter())
										.notifyDataSetChanged();
								saveHistory();
							}
						}).setNegativeButton("Cancel", null).show();
	}

	private int getNewHistoryCount() {
		int counter = 0;
		for (String example_history : getResources().getStringArray(
				R.array.example_searches)) {
			if (mHistory.contains(example_history))
				counter++;
		}
		return mHistory.size() - counter;
	}

	public void clearQuery() {
		mQuery = null;
	}

	private void updateCurrentNavigation() {
		app.CurrentNavigation = Navigation.IDLE;
	}

	protected void search(String query, boolean user) {
		mQuery = query;

		if (mQuery == null) {
			return;
		}

		if (!mHistory.contains(query)) {
			mHistory.add(0, query);
			saveHistory();
		}
		int position = mHistory.indexOf(mQuery);
		getListView().setItemChecked(position, true);

		// make sure the list item is visible
		if (position < getListView().getFirstVisiblePosition())
			getListView().setSelection(position);
		else if (position > getListView().getLastVisiblePosition())
			getListView().setSelectionFromTop(
					getListView().getFirstVisiblePosition()
							+ (position - getListView()
									.getLastVisiblePosition()), 0);

		if (app.DualPane) {
			FragmentPlayerList usattFragment = FragmentPlayerList.getInstance(
					"usatt", query, user);
			FragmentPlayerList rcFragment = FragmentPlayerList.getInstance(
					"rc", query, user);

			FragmentTransaction txn = getFragmentManager().beginTransaction()
					.replace(R.id.usatt, usattFragment, "usatt")
					.replace(R.id.rc, rcFragment, "rc")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			Fragment details = getFragmentManager()
					.findFragmentByTag("details");
			if (details != null) {
				try {
					txn.remove(details);
				} catch (Exception ex) {
					// catch the Illegal State Exception: Fragment not added bug
				}
			}
			txn.commit();
		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity(), ActivityDualPlayerList.class);
			intent.putExtra("query", query);
			intent.putExtra("user", user);
			startActivity(intent);
		}

		if (user)
			app.CurrentNavigation = Navigation.LIST;
	}
}
