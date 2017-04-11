package io.github.izzyleung.zhihudailypurify.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.izzyleung.zhihudailypurify.R;
import io.github.izzyleung.zhihudailypurify.support.Constants;
import io.github.izzyleung.zhihudailypurify.ui.fragment.NewsListFragment;

public class MainActivity extends BaseActivity {
    private static final int PAGE_COUNT = 7;

    @BindView(R.id.main_pager_tabs)
    TabLayout mTabs;
    @BindView(R.id.main_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager.setOffscreenPageLimit(PAGE_COUNT);

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    protected int setContentViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return prepareIntent(PrefsActivity.class);
            case R.id.action_go_to_search:
                return prepareIntent(SearchActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean prepareIntent(Class clazz) {
        startActivity(new Intent(MainActivity.this, clazz));
        return true;
    }

    @OnClick(R.id.fab_pick_date)
    public void onFloatingButtonClick() {
        prepareIntent(PickDateActivity.class);
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Bundle bundle = new Bundle();
            Fragment newFragment = new NewsListFragment();

            Calendar dateToGetUrl = Calendar.getInstance();
            dateToGetUrl.add(Calendar.DAY_OF_YEAR, 1 - i);
            String date = Constants.Dates.simpleDateFormat.format(dateToGetUrl.getTime());

            bundle.putString(Constants.BundleKeys.DATE, date);
            bundle.putBoolean(Constants.BundleKeys.IS_FIRST_PAGE, i == 0);
            bundle.putBoolean(Constants.BundleKeys.IS_SINGLE, false);

            newFragment.setArguments(bundle);
            return newFragment;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar displayDate = Calendar.getInstance();
            displayDate.add(Calendar.DAY_OF_YEAR, -position);

            return (position == 0 ? getString(R.string.zhihu_daily_today) + " " : "")
                    + DateFormat.getDateInstance().format(displayDate.getTime());
        }
    }
}