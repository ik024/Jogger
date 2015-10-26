package com.cos.jogger.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.jogger.R;
import com.cos.jogger.activities.SlidingTabLayout;
import com.cos.jogger.interfaces.IDurationUpdate;
import com.cos.jogger.services.DurationTracker;
import com.cos.jogger.utils.Logger;
import com.cos.jogger.utils.Util;

public class RecordFragment extends Fragment implements IDurationUpdate {

    private static final String TAG = RecordFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    DurationTracker mDurationTracker;
    Intent mDurationTrackerServiceIntent;
    boolean mDurationServiceBinded = false;

    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    SlidingTabLayout tabs;
    RelativeLayout controlRootLayoutStart, controlRootLayoutPauseStop;
    TextView start, stop, pause, resume;


    private ServiceConnection mDurationTrackerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG, "onServiceConnected");
            DurationTracker.LocalBinder binder = (DurationTracker.LocalBinder) service;
            mDurationTracker = binder.getServiceInstance();
            mDurationServiceBinded = true;
            mDurationTracker.registerLister(RecordFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "onServiceDisconnected");
            mDurationServiceBinded = false;
        }
    };

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
        Logger.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.record_layout, container, false);

        //initialize view pager object
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        //get reference of views on the layout
        start = (TextView) view.findViewById(R.id.start);
        pause = (TextView) view.findViewById(R.id.pause);
        stop = (TextView) view.findViewById(R.id.stop);
        resume = (TextView) view.findViewById(R.id.resume);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        controlRootLayoutStart = (RelativeLayout) view.findViewById(R.id.control_root_layout_start);
        controlRootLayoutPauseStop = (RelativeLayout) view.findViewById(R.id.control_root_layout_pasuse_stop);

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

        initializeAllClickListeners();

        mDurationTrackerServiceIntent = new Intent(getActivity(), DurationTracker.class);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume");
        //register listeners
        if(mDurationTracker != null){
            mDurationTracker.registerLister(RecordFragment.this);
        }
        //bind if not binded
        if(!mDurationServiceBinded){
                bindDurationTrackerService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG, "onPause");
        //unregister listners
        if(mDurationTracker != null){
            mDurationTracker.unRegisterLister();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //unbind if bind
        if(mDurationServiceBinded) {
            unbindDurationTrackerService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
        //unbind if bind
        if(mDurationServiceBinded) {
            unbindDurationTrackerService();
        }
    }

    private void initializeAllClickListeners() {

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealPauseStopLayout();
                getActivity().startService(mDurationTrackerServiceIntent);
                if(!mDurationServiceBinded) {
                    bindDurationTrackerService();
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setVisibility(View.GONE);
                resume.setVisibility(View.VISIBLE);
                mDurationTracker.pauseTimer();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setVisibility(View.VISIBLE);
                resume.setVisibility(View.GONE);

                controlRootLayoutStart.setVisibility(View.VISIBLE);
                revealStartLayout();
                mDurationTracker.stopTimer();

                getActivity().stopService(mDurationTrackerServiceIntent);

                if(HomeTab.timerTextView != null) {
                    HomeTab.timerTextView.setText("0:00:00");
                    HomeTab.timermsTextView.setText("000");
                }
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resume.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                mDurationTracker.resumeTimer();
            }
        });

        controlRootLayoutPauseStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to block start view to get click listeners when pause stop is visible
            }
        });
    }

    private void bindDurationTrackerService() {
        getActivity().bindService(mDurationTrackerServiceIntent, mDurationTrackerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindDurationTrackerService(){
        mDurationServiceBinded = false;
        getActivity().unbindService(mDurationTrackerServiceConnection);
        mDurationTracker = null;
    }

    private void revealPauseStopLayout() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = controlRootLayoutStart.getWidth() / 2;
            int cy = controlRootLayoutStart.getHeight() / 2;

            // get the final radius for the clipping circle
            int finalRadius = (int) Math.hypot(controlRootLayoutStart.getWidth(), controlRootLayoutStart.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(controlRootLayoutPauseStop, cx, cy, 0, finalRadius);

            // make the view visible and start the animation
            controlRootLayoutPauseStop.setVisibility(View.VISIBLE);
            anim.setDuration(1000);
            anim.start();
        }else{
            controlRootLayoutStart.setVisibility(View.GONE);
            controlRootLayoutPauseStop.setVisibility(View.VISIBLE);
        }
    }

    private void revealStartLayout() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = (int) (controlRootLayoutStart.getWidth() - Util.dipToPixels(getActivity(), 50));
            int cy = controlRootLayoutStart.getHeight() / 2;

            // get the initial radius for the clipping circle
            int initialRadius = controlRootLayoutStart.getWidth();

            // create the animation (the final radius is zero)
            Animator anim = null;

            anim = ViewAnimationUtils.createCircularReveal(controlRootLayoutPauseStop, cx, cy, initialRadius, 0);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    controlRootLayoutPauseStop.setVisibility(View.GONE);
                }
            });

            // make the view visible and start the animation
            controlRootLayoutStart.setVisibility(View.VISIBLE);

            anim.setDuration(900);

            // start the animation
            anim.start();
        }else{
            controlRootLayoutStart.setVisibility(View.VISIBLE);
            controlRootLayoutPauseStop.setVisibility(View.GONE);
        }
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

    @Override
    public void updateDuration(int hour, int min, int sec, int ms) {
        Logger.d(TAG, "updateDuration");
        if(HomeTab.timerTextView != null) {
            HomeTab.timerTextView.setText("" + hour + ":"
                    + String.format("%02d", min) + ":"
                    + String.format("%02d", sec));
            HomeTab.timermsTextView.setText(""+ms);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        String title[] = {"Home", "Map"};
        int numOfTabs = 2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) // if the position is 0 we are returning the First tab
            {
                Fragment tab1 = HomeTab.newInstance();
                return tab1;
            } else  // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
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
