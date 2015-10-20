package com.cos.jogger.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cos.jogger.R;
import com.cos.jogger.activities.SlidingTabLayout;

public class RecordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    SlidingTabLayout tabs;
    LinearLayout controlRootLayout;
    TextView fab;
    boolean start;

    public static Fragment newInstance() {
        RecordFragment fragment = new RecordFragment();
        return fragment;
    }

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_layout, container, false);
        fab = (TextView) view.findViewById(R.id.fab_start);
        mViewPagerAdapter  = new ViewPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        mViewPager.setAdapter(mViewPagerAdapter);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(mViewPager);

        controlRootLayout = (LinearLayout) view.findViewById(R.id.control_root_layout);
        controlRootLayout.setBackgroundColor(Color.WHITE);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Animator layoutAnimator = null;
                if(!start) {
                    start = true;
                    layoutAnimator = ViewAnimationUtils.createCircularReveal(
                            controlRootLayout,
                            controlRootLayout.getWidth() / 2,
                            controlRootLayout.getHeight() / 2,
                            0,
                            (float) Math.hypot(controlRootLayout.getWidth(), controlRootLayout.getHeight()));
                }else{
                    start = false;
                    layoutAnimator = ViewAnimationUtils.createCircularReveal(
                            controlRootLayout,
                            controlRootLayout.getWidth() / 2,
                            controlRootLayout.getHeight() / 2,
                            (float) Math.hypot(controlRootLayout.getWidth(), controlRootLayout.getHeight()),
                            0);

                }
                controlRootLayout.setVisibility(View.VISIBLE);
                layoutAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                if (controlRootLayout.getVisibility() == View.VISIBLE) {
                    layoutAnimator.setDuration(1000);
                    layoutAnimator.start();
                    controlRootLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    controlRootLayout.setEnabled(true);
                }
                layoutAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(start){
                            controlRootLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }else{
                            controlRootLayout.setBackgroundColor(Color.WHITE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        });
        return view;
    }

    private void animateChangeInColor() {
        final float[] from = new float[3],
                to =   new float[3];

        Color.colorToHSV(Color.parseColor("#FFFFFFFF"), from);   // from white
        Color.colorToHSV(Color.parseColor("#FF4081"), to);     // to pink

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(200);                              // for 300 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                controlRootLayout.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.start();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter{

        String title[] = {"Home", "Map"};
        int numOfTabs = 2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) // if the position is 0 we are returning the First tab
            {
                Fragment tab1 = HomeTab.newInstance();
                return tab1;
            }
            else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                Fragment tab2 = MapTab.newInstance();
                return tab2;
            }
        }

        // This method return the titles for the Tabs in the Tab Strip

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
