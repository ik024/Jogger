package com.cos.jogger.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.jogger.R;
import com.cos.jogger.activities.SlidingTabLayout;
import com.cos.jogger.utils.Util;

public class RecordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    SlidingTabLayout tabs;
    RelativeLayout controlRootLayout;
    TextView start, stop, pause, resume;
    boolean started, enableStartClick, enablePauseClick, enableStopClick, enableResumeClick;

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

        //initialize view pager object
        mViewPagerAdapter  = new ViewPagerAdapter(getChildFragmentManager());

        //get reference of views on the layout
        start = (TextView) view.findViewById(R.id.start);
        pause = (TextView) view.findViewById(R.id.pause);
        stop = (TextView) view.findViewById(R.id.stop);
        resume = (TextView) view.findViewById(R.id.resume);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        controlRootLayout = (RelativeLayout) view.findViewById(R.id.control_root_layout);

        //set pager adapter
        mViewPager.setAdapter(mViewPagerAdapter);

        //will space out the tabs width evenly
        tabs.setDistributeEvenly(true);

        //tabs title color
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getActivity(), R.color.colorAccent);
            }
        });

        //add tabs to view pager
        tabs.setViewPager(mViewPager);

        //initial background color
        controlRootLayout.setBackgroundColor(Color.WHITE);

        initializeAllClickListeners();

        return view;
    }

    private void initializeAllClickListeners() {
        enableStartClick = true;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enableStartClick) {
                    enableStartClick = false;
                    enablePauseClick = true;
                    enableStopClick = true;
                    stop.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.VISIBLE);
                    resume.setVisibility(View.GONE);
                    animateBackgroundColorChange(controlRootLayout.getWidth() / 2, controlRootLayout.getHeight() / 2);
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enablePauseClick) {
                    enablePauseClick = false;
                    enableResumeClick = true;
                    pause.setVisibility(View.GONE);
                    resume.setVisibility(View.VISIBLE);
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enableStopClick) {
                    enableStopClick = false;
                    enablePauseClick = false;
                    enableResumeClick = false;
                    enableStartClick = true;
                    resume.setVisibility(View.GONE);
                    animateBackgroundColorChange((controlRootLayout.getWidth() - ((int) Util.dipToPixels(getActivity(), 30) + (stop.getWidth() / 2))),
                            controlRootLayout.getHeight() / 2);
                }
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enableResumeClick) {
                    enableResumeClick = false;
                    enablePauseClick = true;
                    resume.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void animateBackgroundColorChange(int centerX, int centerY){
        Animator layoutAnimator = null;
        if(!started) {
            started = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                layoutAnimator = ViewAnimationUtils.createCircularReveal(
                        controlRootLayout,
                        centerX,
                        centerY,
                        0,
                        (float) Math.hypot(controlRootLayout.getWidth(), controlRootLayout.getHeight()));

            }else{
                layoutAnimator = ObjectAnimator.ofInt(controlRootLayout,
                        "backgroundColor",
                        Color.parseColor("#ffffff"),
                        Color.parseColor("#FF4081"));
            }
        }else{
            started = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                layoutAnimator = ViewAnimationUtils.createCircularReveal(
                        controlRootLayout,
                        centerX,
                        centerY,
                        (float) Math.hypot(controlRootLayout.getWidth(), controlRootLayout.getHeight()),
                        0);
            }else{
                layoutAnimator = ObjectAnimator.ofInt(controlRootLayout,
                        "backgroundColor",
                        Color.parseColor("#FF4081"),
                        Color.parseColor("#ffffff"));
            }

        }
        controlRootLayout.setVisibility(View.VISIBLE);
        layoutAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (controlRootLayout.getVisibility() == View.VISIBLE) {
            layoutAnimator.setDuration(1000);
            layoutAnimator.start();
            controlRootLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            controlRootLayout.setEnabled(true);
        }
        layoutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (started) {
                    controlRootLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                } else {
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
            else  // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
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
