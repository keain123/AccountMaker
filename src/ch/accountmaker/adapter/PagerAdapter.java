package ch.accountmaker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ch.accountmaker.view.StartPageFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

	List<Fragment> mFragments;
	List<String> mTabs;

	public PagerAdapter(FragmentManager fm, List<String> tabs) {
		super(fm);
		mTabs = tabs;
		mFragments = new ArrayList<>();
		mFragments.add(StartPageFragment.newInstance());

	}

	public void addPager(String tab, Fragment fragment) {
		mTabs.add(tab);
		mFragments.add(fragment);
		setFragment(tab, fragment);

	}

	private void setFragment(String tab, Fragment f) {
		for (int i = 0; i < mTabs.size(); i++)
			if (mTabs.get(i).equals(tab)) {
				mFragments.set(i, f);
				break;
			}
	}

	@Override
	public Fragment getItem(int position) {

		return mFragments.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTabs.get(position).toString();
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}
}