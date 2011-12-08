package wei.mark.pingpongboss.util;

import java.util.HashMap;

import wei.mark.pingpongboss.fragment.PlayerFriendsFragment;
import wei.mark.pingpongboss.fragment.PlayerSearchFragment;
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
		fragmentPosition.put(0, PlayerSearchFragment.TAG);
		fragmentPosition.put(1, PlayerFriendsFragment.TAG);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		String fragmentTag = fragmentPosition.get(position);
		if (fragmentTag.equals(PlayerSearchFragment.TAG))
			fragment = new PlayerSearchFragment();
		else if (fragmentTag.equals(PlayerFriendsFragment.TAG))
			fragment = new PlayerFriendsFragment();

		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public String getTitle(int position) {
		String fragment = fragmentPosition.get(position);
		if (fragment.equals(PlayerSearchFragment.TAG))
			return "Search";
		else if (fragment.equals(PlayerFriendsFragment.TAG))
			return "Friends";
		else
			return null;
	}

	public static HashMap<Integer, String> getFragmentPosition() {
		return fragmentPosition;
	}

}
