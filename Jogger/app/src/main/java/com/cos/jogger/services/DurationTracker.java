package com.cos.jogger.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;

import com.cos.jogger.R;
import com.cos.jogger.activities.HomeActivity;
import com.cos.jogger.interfaces.IDurationUpdate;
import com.cos.jogger.models.Recorder;
import com.cos.jogger.utils.Logger;

public class DurationTracker extends Service {

    public static Recorder mRecorder;

    private static final String TAG = DurationTracker.class.getSimpleName();
    private static final int NOTIFICATION_ID = 9000;
    private final IBinder mBinder = new LocalBinder();

    private static Handler customHandler = null;

    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;


    private static IDurationUpdate updateTimer;

    public DurationTracker() {
    }

    public void registerLister(IDurationUpdate iDurationUpdate){
        Logger.d(TAG, "registerLister");
        updateTimer = iDurationUpdate;
    }

    public void unRegisterLister(){
        Logger.d(TAG, "unRegisterLister");
        updateTimer = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand");
        if(mRecorder == null) {
            mRecorder = new Recorder();
        }

        runAsForeground();

        startTimer();

        return START_STICKY;
    }

    private Runnable stopWatch = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            if(updateTimer != null){
                updateTimer.updateDuration(00, +mins, secs, milliseconds);
            }

            customHandler.postDelayed(this, 0);
        }

    };

    private void startTimer(){
        Logger.d(TAG, "startTimer");
        mRecorder.setState(Recorder.State.Running);

        startTime = SystemClock.uptimeMillis();
        if(customHandler == null) {
            customHandler = new Handler();
        }
        customHandler.postDelayed(stopWatch, 0);
    }

    public void resumeTimer(){
        Logger.d(TAG, "resumeTimer");
        mRecorder.setState(Recorder.State.Running);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(stopWatch, 0);
    }

    public void pauseTimer(){
        Logger.d(TAG, "pauseTimer");
        mRecorder.setState(Recorder.State.Paused);
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(stopWatch);
    }

    public void stopTimer(){
        Logger.d(TAG, "stopTimer");
        mRecorder = null;
        cancelNotification(this, NOTIFICATION_ID);
        customHandler.removeCallbacks(stopWatch);
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        updateTimer.updateDuration(00, 00, 00, 00);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //returns the instance of the service
    public class LocalBinder extends Binder {
        public DurationTracker getServiceInstance(){
            return DurationTracker.this;
        }
    }

    private void runAsForeground(){
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
                notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification notification=new NotificationCompat.Builder(this)
                .setColor(Color.BLUE)
                .setSmallIcon(R.drawable.nav_settings)
                .setContentTitle("Tracking...")
                .setContentText("Keep running !")
                .setContentIntent(pendingIntent).build();

        startForeground(NOTIFICATION_ID, notification);

    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
