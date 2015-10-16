package com.cos.jogger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cos.jogger.BuildConfig;
import com.cos.jogger.utils.Logger;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;

public class Splash extends AppCompatActivity {

    private final String TAG = Splash.class.getSimpleName();

    AccessTokenTracker mAccessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Logger.d(TAG, "Old AccessToken: " + currentAccessToken);
                Logger.d(TAG, "Current AccessToken: " + currentAccessToken);
                callLoginOrHomeActivity(currentAccessToken);
            }
        };

        mAccessTokenTracker.startTracking();

        //Spleep the thread in order to get enough time to access Facebook's AccessToken
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void callLoginOrHomeActivity(AccessToken accessToken){
        Logger.d(TAG, "callLoginOrHomeActivity called");
        if(accessToken != null){//user already logged in go to HomeActivity
            Intent intent = new Intent(Splash.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else{//user not logged in go to LoginActivty
            Intent intent = new Intent(Splash.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if(mAccessTokenTracker != null) {
            mAccessTokenTracker.startTracking();
        }
        super.onDestroy();
    }
}
