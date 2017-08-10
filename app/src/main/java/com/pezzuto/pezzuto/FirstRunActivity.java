package com.pezzuto.pezzuto;

import android.content.res.Configuration;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pezzuto.pezzuto.listeners.OnFirstRunInteractionListener;

public class FirstRunActivity extends FragmentActivity implements OnFirstRunInteractionListener {
    private static final int NUM_PAGES = 4;
    private NonSwipeableViewPager pager;
    private TabLayout tabLayout;
    public static final int CLOSE_APP = 1;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);
        pager = (NonSwipeableViewPager) findViewById(R.id.photos_viewpager);
        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager, true);
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }
    public void goOn() {
        pager.setCurrentItem(pager.getCurrentItem()+1);
    }
    @Override
    public void onBackPressed()
    {
        if (pager.getCurrentItem() > 0) pager.setCurrentItem(pager.getCurrentItem()-1);
        else {
            setResult(CLOSE_APP);
            finish();
        }
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ScreenSlidePageFragment.newInstance("welcome");
                case 1:
                    return ScreenSlidePageFragment.newInstance("remain_updated");
                case 2:
                    return ScreenSlidePageFragment.newInstance("events");
                case 3:
                    return new ClientAuthenticationFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    public void hideTabLayout() {
        tabLayout.setVisibility(View.INVISIBLE);
    }
    public void showTabLayout() {
        tabLayout.setVisibility(View.VISIBLE);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            hideTabLayout();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            showTabLayout();
        }
    }

}


