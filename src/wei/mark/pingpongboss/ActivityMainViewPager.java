package wei.mark.pingpongboss;

import wei.mark.pingpongboss.util.MainFragmentAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.PageIndicator;

public class ActivityMainViewPager extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		ViewPager viewPager = (ViewPager) findViewById(R.id.mainViewPager);
		viewPager.setAdapter(new MainFragmentAdapter(
				getSupportFragmentManager()));

		PageIndicator indicator = (PageIndicator) findViewById(R.id.titles);
		indicator.setViewPager(viewPager);

	}
}
