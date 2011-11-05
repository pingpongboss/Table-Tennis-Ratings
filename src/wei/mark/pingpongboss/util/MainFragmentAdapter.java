package wei.mark.pingpongboss.util;

import wei.mark.pingpongboss.FragmentPlayerFriends;
import wei.mark.pingpongboss.FragmentPlayerSearch;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.TitleProvider;

public class MainFragmentAdapter extends FragmentPagerAdapter implements
		TitleProvider {

	public MainFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new FragmentPlayerSearch();
		case 1:
			return new FragmentPlayerFriends();
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public String getTitle(int position) {
		switch (position) {
		case 0:
			return "Search";
		case 1:
			return "Friends";
		default:
			return null;
		}
	}

}
