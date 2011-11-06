package wei.mark.pingpongboss;

import java.util.HashSet;
import java.util.Set;

import wei.mark.pingpongboss.util.MainFragmentAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.viewpagerindicator.TitlePageIndicator;

public class ActivityMainViewPager extends FragmentActivity {
	PingPongBoss app;
	MainOnPageChangeListener viewPagerListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (PingPongBoss) getApplication();

		setContentView(R.layout.main);

		ViewPager viewPager = (ViewPager) findViewById(R.id.mainViewPager);
		viewPager.setAdapter(new MainFragmentAdapter(
				getSupportFragmentManager()));

		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
		indicator.setViewPager(viewPager);

		viewPagerListener = new MainOnPageChangeListener();
		viewPagerListener.addOnPageChangeListener(indicator);

		viewPager.setOnPageChangeListener(viewPagerListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		app.facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public class MainOnPageChangeListener implements OnPageChangeListener {
		Set<OnPageChangeListener> listeners;

		public MainOnPageChangeListener() {
			listeners = new HashSet<OnPageChangeListener>();
		}

		public void addOnPageChangeListener(OnPageChangeListener listener) {
			listeners.add(listener);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			for (OnPageChangeListener listener : listeners) {
				try {
					listener.onPageScrollStateChanged(arg0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			for (OnPageChangeListener listener : listeners) {
				try {
					listener.onPageScrolled(arg0, arg1, arg2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onPageSelected(int arg0) {
			for (OnPageChangeListener listener : listeners) {
				try {
					listener.onPageSelected(arg0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public MainOnPageChangeListener getViewPagerListener() {
		return viewPagerListener;
	}
}
