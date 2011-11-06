package wei.mark.pingpongboss.util;

import java.util.HashMap;

import wei.mark.pingpongboss.FragmentPlayerFriends;
import wei.mark.pingpongboss.FragmentPlayerSearch;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.TitleProvider;

public class MainFragmentAdapter extends FragmentPagerAdapter implements
		TitleProvider {
	static HashMap<Integer, String> fragmentPosition;

	public MainFragmentAdapter(FragmentManager fm) {
		super(fm);

		fragmentPosition = new HashMap<Integer, String>();
		fragmentPosition.put(0, FragmentPlayerSearch.TAG);
		fragmentPosition.put(1, FragmentPlayerFriends.TAG);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		String fragmentTag = fragmentPosition.get(position);
		if (fragmentTag.equals(FragmentPlayerSearch.TAG))
			fragment = new FragmentPlayerSearch();
		else if (fragmentTag.equals(FragmentPlayerFriends.TAG))
			fragment = new FragmentPlayerFriends();
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public String getTitle(int position) {
		String fragment = fragmentPosition.get(position);
		if (fragment.equals(FragmentPlayerSearch.TAG))
			return "Search";
		else if (fragment.equals(FragmentPlayerFriends.TAG))
			return "Friends";
		else
			return null;
	}

	public static HashMap<Integer, String> getFragmentPosition() {
		return fragmentPosition;
	}

}
