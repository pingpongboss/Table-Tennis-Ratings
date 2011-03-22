package wei.mark.tabletennis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class FragmentProgressBar extends DialogFragment {
	String mTitle, mMessage;

	public static FragmentProgressBar getInstance(String title, String message) {
		FragmentProgressBar fragment = new FragmentProgressBar();
		Bundle b = new Bundle();
		b.putString("title", title);
		b.putString("message", message);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTitle = getArguments().getString("title");
		mMessage = getArguments().getString("message");
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
		((TextView) v.findViewById(R.id.title)).setText(mTitle);
		((TextView) v.findViewById(R.id.message)).setText(mMessage);

		return v;
	}
}
