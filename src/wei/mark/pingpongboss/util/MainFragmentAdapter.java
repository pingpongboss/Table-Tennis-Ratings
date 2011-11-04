package wei.mark.pingpongboss.util;

import com.viewpagerindicator.TitleProvider;

import wei.mark.pingpongboss.FragmentPlayerList;
import wei.mark.pingpongboss.FragmentPlayerSearch;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
			return FragmentPlayerList.getInstance("rc", "Wei, Mark", true);
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
