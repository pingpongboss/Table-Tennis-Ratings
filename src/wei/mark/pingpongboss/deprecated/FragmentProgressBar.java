package wei.mark.pingpongboss.deprecated;

import wei.mark.pingpongboss.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentProgressBar extends DialogFragment {
	String mTitle, mMessage;
	int mProgressState;

	TextView titleTextView, messageTextView;
	ProgressBar progressBar;

	public static FragmentProgressBar getInstance(String title, String message,
			int progressState) {
		FragmentProgressBar fragment = new FragmentProgressBar();
		Bundle b = new Bundle();
		b.putString("title", title);
		b.putString("message", message);
		b.putInt("progressstate", progressState);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTitle = getArguments().getString("title");
		mMessage = getArguments().getString("message");
		mProgressState = getArguments().getInt("progressstate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (getShowsDialog())
			return null;
		return createView(inflater, container);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity()).setCancelable(false)
				.setView(createView(null, null)).create();
	}

	private View createView(LayoutInflater inflater, ViewGroup container) {
		if (inflater == null)
			inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.fragment_progress_bar, container,
				false);

		titleTextView = (TextView) v.findViewById(R.id.title);

		messageTextView = (TextView) v.findViewById(R.id.message);

		progressBar = (ProgressBar) v.findViewById(R.id.progress);

		initializeViews();

		return v;
	}

	private void initializeViews() {
		setTitle(mTitle);
		setMessage(mMessage);
		setProgressState(mProgressState);
	}

	public void setTitle(String title) {
		mTitle = title;
		titleTextView.setText(mTitle);
	}

	public void setMessage(String message) {
		mMessage = message;
		messageTextView.setText(mMessage);
	}

	public void setProgressState(int progressState) {
		mProgressState = progressState;
		switch (ProgressBarState.getState(mProgressState)) {
		case GONE:
			progressBar.setVisibility(View.GONE);
			break;
		case INDETERMINATE:
			progressBar.setIndeterminate(true);
			break;
		case DETERMINATE:
			progressBar.setIndeterminate(false);
			progressBar.setProgress(mProgressState);
			break;
		}
	}

	public enum ProgressBarState {
		GONE(-2), INDETERMINATE(-1), DETERMINATE(0);

		int code;

		ProgressBarState(int state) {
			code = state;
		}

		public int getCode() {
			return code;
		}

		static public ProgressBarState getState(int state) {
			switch (state) {
			case -2:
				return GONE;
			case -1:
				return INDETERMINATE;
			default:
				if (state < 0)
					return INDETERMINATE;
				return DETERMINATE;
			}
		}
	}
}
