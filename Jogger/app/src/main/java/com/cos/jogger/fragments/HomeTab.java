package com.cos.jogger.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cos.jogger.R;
import com.cos.jogger.services.DurationTracker;

public class HomeTab extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static TextView timerTextView, timermsTextView;

    public static Fragment newInstance() {
        HomeTab fragment = new HomeTab();
        return fragment;
    }

    public HomeTab() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_tab, container, false);
        timerTextView = (TextView) view.findViewById(R.id.timer);
        timermsTextView = (TextView) view.findViewById(R.id.timerms);
        if(DurationTracker.mRecorder != null) {
            timerTextView.setText("" + DurationTracker.mRecorder.getHour() + ":"
                    + String.format("%02d", DurationTracker.mRecorder.getMinute()) + ":"
                    + String.format("%02d", DurationTracker.mRecorder.getSecond()));
            timermsTextView.setText(""+DurationTracker.mRecorder.getMilliseconds());
        }
        return view;
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

}
